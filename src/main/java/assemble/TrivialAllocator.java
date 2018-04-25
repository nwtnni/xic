package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;
import xic.XicInternalException;

public class TrivialAllocator extends InsVisitor<Void> {

    public static CompUnit allocate(CompUnit unit) {
        TrivialAllocator allocator = new TrivialAllocator(unit);
        return allocator.allocate();
    }

    // Running list of assembly instructions
    private CompUnit unit;

    // Current list of instructions
    private List<Instr> instrs;

    // Map of named temps to offset on stack
    private Map<String, Integer> tempStack;

    // Number of temps on the stack - 1
    private int tempCounter;

    // Maximum number of args to a call in current function
    private int maxArgs;

    // Maximum number of returns from a call in current function
    private int maxRets;

    // Number of words to subtract from base pointer to get location
    // in stack where multiple returns > 2 must be written by callee.
    private Operand calleeReturnAddr;

    // Number of words to add to rsp to get location in stack where 
    // multiple returns > 2 are accessed by caller.
    private int callerReturnAddr;

    // Caller saved registers - not required for trivial allocation
    // private Operand r10;
    // private Operand r11;

    private TrivialAllocator(CompUnit unit) {
        this.unit = unit;
        
        this.instrs = new ArrayList<>();
        this.tempStack = new HashMap<>();
        this.tempCounter = 0;
        this.maxArgs = 0;
        this.maxRets = 0;
        // this.isMultiple = 0;
        this.calleeReturnAddr = null;
        this.callerReturnAddr = -1;
    }

    /**
     * Push a named temp to the stack.
     */
    private Operand pushTemp(String name) {
        tempStack.put(name, tempCounter++);
        return getTemp(name);
    }

    /**
     * Push an unnamed temp to the stack and return the mem operand.
     */
    private Operand pushTemp() {
        return Operand.mem(Operand.RBP, -normalize(++tempCounter));
    }

    /**
     * Get the mem operand to a temp on the stack.
     * Equivalent to -(i+1)*8(%rbp) where name -> i in the tempStack
     * +1 to offset for saved base pointer
     */
    private Operand getTemp(String name) {
        if (tempStack.containsKey(name)) {
            return Operand.mem(Operand.RBP, -normalize(tempStack.get(name) + 1));
        }
        throw XicInternalException.runtime("Non-existent temp. Check tiler.");
    }

    /**
     * Multiply by the word size to get offset for a memory location.
     */
    private int normalize(int i) {
        return i * Config.WORD_SIZE;
    }

    /* Recursive descent visitors */

    private CompUnit allocate() {
        for (FuncDecl fn : unit.fns) {
            allocate(fn);
        }
        return unit;
    }

    private void allocate(FuncDecl fn) {
        instrs = new ArrayList<>();
        tempStack = new HashMap<>();
        tempCounter = 0;
        maxArgs = 0;
        maxRets = 0;
        calleeReturnAddr = null;

        // Store address to put multiple returns from arg0 to stack
        if (fn.rets > 2) {
            calleeReturnAddr = pushTemp();
        }

        for (Instr i : fn.stmts) {
            i.accept(this);
        }
        fn.stmts = instrs;

        // Calculate words to shift rsp, +1 to offset tempCounter
        int rsp = tempCounter + maxArgs + maxRets + 1;
        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Operand shift = Operand.imm(normalize(rsp));

        // Insert stack setup 
        BinOp sub = new BinOp(Kind.SUB, Operand.RSP, shift);
        fn.prelude.set(7, sub);

        // Insert stack teardown
        BinOp add = new BinOp(Kind.ADD, Operand.RSP, shift);
        fn.epilogue.set(2, add);

    }

    public Void visit(BinOp op) {
        assert op.dest == null && op.src == null;

        Operand dest = allocate(op.destTemp);
        op.dest = dest;

        Operand src = allocate(op.srcTemp);
        // Insert mov when performing mem to mem or when imm > 32 bits
        // TODO: add reg alloc for shift instructions
        // Shift instructions only use imm8 or CL
        if (src.isMem() && dest.isMem() || (src.isImm() && !Config.within(32, src.value()))) {
            instrs.add(new Mov(Operand.RAX, src));
            src = Operand.RAX;
        }
        op.src = src;
        
        instrs.add(op);
        return null;
    }

    public Void visit(Call call) {
        maxArgs = Math.max(maxArgs, call.numArgs);
        maxRets = Math.max(maxRets, call.numRet);

        callerReturnAddr = Math.max(call.numArgs - 6, 0) + call.numRet - 2 - 1;

        // Hoist args out of call into list of arguments
        for (Instr arg : call.args) {
            arg.accept(this);
        }
        call.args = new ArrayList<>();

        instrs.add(call);
        return null;
    }

    public Void visit(Cmp cmp) {
        Operand left = allocate(cmp.leftTemp);
        Operand right = allocate(cmp.rightTemp);

        if (left.isMem() && right.isMem() || (left.isImm() && !Config.within(32, left.value()))) {
        instrs.add(new Mov(Operand.RAX, left));
            left = Operand.RAX;
        }
        cmp.left = left;

        if (right.isImm()) {
        instrs.add(new Mov(Operand.R11, right));
            right = Operand.R11;
        }
        cmp.right = right;

        instrs.add(cmp);
        return null;
    }

    public Void visit(Cqo i) {
        instrs.add(i);
        return null;
    }

    public Void visit(DivMul op) {
        Operand src = allocate(op.srcTemp);
        if (src.isImm()) {
            instrs.add(new Mov(Operand.R11, src));
            src = Operand.R11;
        }
        op.src = src;

        op.dest = allocate(op.destTemp);

        instrs.add(op);
        return null;
    }

    public Void visit(Jcc i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Jmp i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Label i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Lea lea) {
        lea.dest = allocate(lea.destTemp);
        lea.src = allocate(lea.srcTemp);

        instrs.add(lea);
        return null;
    }

    public Void visit(Mov mov) {
        boolean destIsMem = mov.destTemp.trivialIsMem();

        Operand src = allocate(mov.srcTemp);
        if ((src.isMem() && destIsMem) || (src.isImm() && !Config.within(32, src.value()))) {
            instrs.add(new Mov(Operand.RAX, src));
            src = Operand.RAX;
        }
        mov.src = src;

        mov.dest = allocate(mov.destTemp);

        instrs.add(mov);
        return null;
    }

    public Void visit(Pop i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Push i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Ret i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Set i) {
        instrs.add(i);
        return null;
    }

    public Void visit(Text i) {
        instrs.add(i);
        return null;
    }

    private Operand allocate(Temp t) {
        String name = t.name;
        int i = 0;
        switch (t.kind) {
            // Allocate an immediate value
            case IMM:
                return Operand.imm(t.value);

            // Allocate an ordinary temporary
            case TEMP:
                if (!tempStack.containsKey(name)) {
                    return pushTemp(name);
                }
                return getTemp(name);

            // Allocate a memory access off a base register
            case MEM:
                Temp base = t.base;
                assert base != null;
                instrs.add(new Mov(Operand.R11, getTemp(base.name)));
                return Operand.mem(Operand.R11);
            
            // Allocate a memory access of a base register and offset
            case MEMBR:
                base = t.base;
                assert base != null;
                instrs.add(new Mov(Operand.R11, getTemp(base.name)));
                return Operand.mem(Operand.R11, t.offset);

            // Allocate a memory access of a 2 registers with scale and an offset
            case MEMSBR:
                base = t.base;
                assert base != null;
                Temp reg = t.reg;
                assert reg != null;
                instrs.add(new Mov(Operand.R11, getTemp(base.name)));
                instrs.add(new Mov(Operand.R10, getTemp(reg.name)));
                return Operand.mem(Operand.R11, Operand.R10, t.offset, t.scale);
            
            // Allocate an argument
            case ARG:
                i = (int) t.number;
                if (i < 6) {
                    return Config.getArg(i);
                }

                if (t.callee) {
                    // If accessing arg as callee, then read from above the basepointer
                    // -6 for regs, +1 for base pointer, +1 for return addr
                    int offset = normalize(i - 6 + 1 + 1);
                    return Operand.mem(Operand.RBP, offset);
                } else {
                    // If writing to arg as caller, then write to above the stackpointer
                    // -6 for regs
                    int offset = normalize(i - 6);
                    return Operand.mem(Operand.RSP, offset);
                }

            // Allocate a return
            case RET:
                i = (int) t.number;
                if (i == 0) {
                    return Operand.RAX;
                } else if (i == 1) {
                    return Operand.RDX;
                }
                if (t.callee) {
                    // If writing returns as callee, write to multiple return addr
                    // -2 for regs
                    int offset = -normalize(i - 2);
                    instrs.add(new Mov(Operand.R11, calleeReturnAddr));
                    return Operand.mem(Operand.R11, offset);
                } else {
                    // If reading returns as caller, read from return address
                    // -2 for regs
                    int offset = normalize(callerReturnAddr - (i - 2));
                    return Operand.mem(Operand.RSP, offset);
                }

            // Get the address for multiple returns
            case MULT_RET:
                if (t.callee) {
                    // Return the memory address to write multiple returns to
                    // as the callee
                    return calleeReturnAddr;
                } else {
                    // Return the memory address calcuated by the caller before
                    // making a call
                    return Operand.mem(Operand.RSP, normalize(callerReturnAddr));
                }

            // Get the fixed register
            case FIXED:
                return t.register;
        }
        assert false;
        return null;
    }
}