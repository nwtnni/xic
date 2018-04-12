package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assemble.*;
import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;
import ir.IRBinOp;
import xic.XicInternalException;

public class TrivialAllocator {

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

    private TrivialAllocator(CompUnit unit) {
        this.unit = unit;
        
        this.instrs = new ArrayList<>();
        this.tempStack = new HashMap<>();
        this.tempCounter = 0;
        this.maxArgs = 0;
        this.maxRets = 0;
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
        throw XicInternalException.internal("Non-existent temp. Check assembly gen.");
    }

    /**
     * Multiply by the word size to get offset for a memory location.
     */
    private int normalize(int i) {
        return i * Config.WORD_SIZE;
    }

    private CompUnit allocate() {
        for (FuncDecl fn : unit.fns) {
            System.out.println("alloc for " + fn.name);
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
            calleeReturnAddr = pushTemp(Config.CALLEE_MULT_RETURN.name);
            Operand dest = calleeReturnAddr;
            Operand src = Config.getArg(0);
            Mov mov = new Mov(dest, src);
            fn.prelude.set(8, mov);
        }

        for (Instr i : fn.stmts) {
            allocate(i);
        }
        fn.stmts = instrs;

        // Calculate words to shift rsp, +1 to offset tempCounter
        int rsp = tempCounter + maxArgs + maxRets + 1;
        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Operand shift = Operand.imm(normalize(rsp));

        // Insert stack setup 
        BinOp sub = new BinOp(Kind.SUB, Operand.RSP, shift, Operand.RSP);
        fn.prelude.set(7, sub);

        // Insert stack teardown
        BinOp add = new BinOp(Kind.ADD, Operand.RSP, shift, Operand.RSP);
        fn.epilogue.set(2, add);

    }

    private Operand allocate(Instr ins) {
        if (ins instanceof BinOp) {
            BinOp op = (BinOp) ins;
            op.dest = allocate(op.destTemp);
            op.left = allocate(op.leftTemp);
            Operand right = allocate(op.rightTemp);
            if (right.isImm() && Math.abs(right.value()) > Math.pow(2, 31) - 1) {
                Operand tmp = pushTemp();
                instrs.add(new Mov(tmp, right));
                op.right = tmp;
            } else {
                op.right = right;
            }
        } else if (ins instanceof BinMul) {
            BinMul op = (BinMul) ins;
            op.dest = allocate(op.destTemp);
            op.left = allocate(op.leftTemp);
            Operand right = allocate(op.rightTemp);
            if (right.isImm()) {
                Operand tmp = pushTemp();
                instrs.add(new Mov(tmp, right));
                op.right = tmp;
            } else {
                op.right = right;
            }
        } else if (ins instanceof BinCmp) {
            BinCmp op = (BinCmp) ins;
            op.dest = allocate(op.destTemp);
            op.left = allocate(op.leftTemp);
            Operand right = allocate(op.rightTemp);
            if (right.isImm()) {
                Operand tmp = pushTemp();
                instrs.add(new Mov(tmp, right));
                op.right = tmp;
            } else {
                op.right = right;
            }
        } else if (ins instanceof Call) {

        } else if (ins instanceof Cmp) {

        } else if (ins instanceof Jcc) {

        } else if (ins instanceof Jmp) {

        } else if (ins instanceof Label) {

        } else if (ins instanceof Lea) {

        } else if (ins instanceof Mov) {

        } else if (ins instanceof Pop) {

        } else if (ins instanceof Push) {

        } else if (ins instanceof Ret) {

        } else if (ins instanceof Text) {
            
        }
        instrs.add(ins);
        return null;
    }

    private Operand allocate(Temp t) {
        switch (t.kind) {
            case IMM:
                // TODO: add checks for imms based on parent instruction
                // ADD, SUB, CMP don't allow imm64
                // IDIV and IMUL don't use imms
                // MOV only imm64 -> reg
                // SHIFT only imm8
                return Operand.imm(t.value);
            case TEMP:
                String name = t.name;
               
                // If temp is an argument
                String regex = String.format("(%s)(\\d)", Config.ABSTRACT_ARG_PREFIX);
                Matcher arg = Pattern.compile(regex).matcher(name);
                if (arg.find()) {
                    int i = Integer.parseInt(arg.group(1));
                    if (i < 6) {
                        return Config.getArg(i);
                    }

                    // -6 for regs, +1 to move above rbp
                    int offset = normalize(i - 6 + 1);
                    return Operand.mem(Operand.RBP, offset);
                } 
                
                // If temp is a return
                regex = String.format("(%s)(\\d)", Config.ABSTRACT_RET_PREFIX);
                Matcher ret = Pattern.compile(regex).matcher(name); 
                if (ret.find()) {
                    int i = Integer.parseInt(arg.group(1));
                    if (i == 0) {
                        return Operand.RAX;
                    } else if (i == 1) {
                        return Operand.RDX;
                    }
                    // -2 for regs
                    int offset = normalize(callerReturnAddr - (i - 2));
                    return Operand.mem(Operand.RSP, offset);
                }

                // Generic named temp
                if (!tempStack.containsKey(name)) {
                    pushTemp(name);
                }
                return getTemp(name);
        }
        assert false;
        return null;
    }
}