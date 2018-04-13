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

    // 1 if current function has multiple returns else 0
    private int isMultiple;

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
        this.isMultiple = 0;
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

    /**
     * Check if value is representable with n-bits in 2's complement notation
     */
    private boolean within(int bits, long value) {
        return Math.abs(value) > Math.pow(2, bits - 1) - 1;
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
        isMultiple = 0;
        calleeReturnAddr = null;

        // Store address to put multiple returns from arg0 to stack
        if (fn.rets > 2) {
            isMultiple = 1;
            calleeReturnAddr = pushTemp();
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
        int rsp = tempCounter + maxArgs + maxRets + 16;
        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Operand shift = Operand.imm(normalize(rsp));

        // Insert stack setup 
        Sub sub = new Sub(Operand.RSP, shift);
        fn.prelude.set(7, sub);

        // Insert stack teardown
        Add add = new Add(Operand.RSP, shift);
        fn.epilogue.set(2, add);

    }

    private Operand allocate(Instr ins) {
        if (ins instanceof BinOp) {
            BinOp op = (BinOp) ins;
            Operand dest = allocate(op.destTemp);
            op.dest = Operand.RAX;

            Operand left = allocate(op.leftTemp);
            op.left = left;

            Operand right = allocate(op.rightTemp);
            op.right = right;
            
            instrs.add(ins);
            instrs.add(new Mov(dest, Operand.RAX));

        } else if (ins instanceof BinMul) {
            BinMul op = (BinMul) ins;
            op.dest = allocate(op.destTemp);

            Operand left = allocate(op.leftTemp);
            op.left = left;

            Operand right = allocate(op.rightTemp);
            instrs.add(new Mov(Operand.RDX, right));
            op.right = Operand.RDX;

            instrs.add(ins);

        } else if (ins instanceof BinCmp) {
            BinCmp op = (BinCmp) ins;
            op.dest = allocate(op.destTemp);

            Operand left = allocate(op.leftTemp);
            instrs.add(new Mov(Operand.RAX, left));
            op.left = Operand.RAX;

            Operand right = allocate(op.rightTemp);
            instrs.add(new Mov(Operand.RDX, right));
            op.right = Operand.RDX;

            instrs.add(ins);

        } else if (ins instanceof Call) {
            Call call = (Call) ins;

            // Undo offset for multiple return when making a call
            int saved = isMultiple;
            isMultiple = 0;

            maxArgs = Math.max(maxArgs, call.numArgs);
            maxRets = Math.max(maxRets, call.numRet);

            callerReturnAddr = Math.max(call.numArgs - 6, 0) + call.numRet - 2 - 1;

            // Hoist args out of call into list of arguments
            for (Instr arg : call.args) {
                allocate(arg);
            }
            call.args = new ArrayList<>();

            // Reset offset for multiple return
            isMultiple = saved;

            instrs.add(ins);

        } else if (ins instanceof Cmp) {
            Cmp cmp = (Cmp) ins;
            Operand left = allocate(cmp.leftTemp);
            instrs.add(new Mov(Operand.RAX, left));
            cmp.left = Operand.RAX;

            Operand right = allocate(cmp.rightTemp);
            instrs.add(new Mov(Operand.R10, right));
            cmp.right = Operand.R10;
            
            instrs.add(ins);

        } else if (ins instanceof Jcc) {
            instrs.add(ins);

        } else if (ins instanceof Jmp) {
            instrs.add(ins);

        } else if (ins instanceof Label) {
            instrs.add(ins);

        } else if (ins instanceof Lea) {
            Lea lea = (Lea) ins;
            lea.dest = allocate(lea.destTemp);

            Operand addr = allocate(lea.srcTemp);
            lea.src = addr;

            instrs.add(ins);

        } else if (ins instanceof Mov) {
            Mov mov = (Mov) ins;
            if (mov.dest == null) {
                mov.dest = allocate(mov.destTemp);

                Operand src = allocate(mov.srcTemp);
                instrs.add(new Mov(Operand.RAX, src));
                mov.src = Operand.RAX;
            }
            instrs.add(ins);

        } else if (ins instanceof Pop) {
            instrs.add(ins);

        } else if (ins instanceof Push) {
            instrs.add(ins);

        } else if (ins instanceof Ret) {
            instrs.add(ins);

        } else if (ins instanceof Text) {
            instrs.add(ins);
        }

        return null;
    }

    private Operand allocate(Temp t) {

        String name = t.name;
        int i = 0;
        switch (t.kind) {
            case IMM:
                // TODO: add checks for imms based on parent instruction
                // ADD, SUB, CMP don't allow imm64
                // IDIV and IMUL don't use imms
                // MOV only imm64 -> reg
                // SHIFT only imm8
                return Operand.imm(t.value);
            case TEMP:
                if (!tempStack.containsKey(name)) {
                    return pushTemp(name);
                }
                return getTemp(name);
            case ARG:
                i = (int) t.value + isMultiple;
                if (i < 6) {
                    return Config.getArg(i);
                }

                if (t.callee) {
                    // -6 for regs, +1 for base pointer, +1 for return addr
                    int offset = normalize(i - 6 + 1 + 1);
                    return Operand.mem(Operand.RBP, offset);
                } else {
                    int offset = normalize(i - 6);
                    return Operand.mem(Operand.RSP, offset);
                }
            case RET:
                i = (int) t.value;
                if (i == 0) {
                    return Operand.RAX;
                } else if (i == 1) {
                    return Operand.RDX;
                }
                // -2 for regs
                if (t.callee) {
                    int offset = -normalize(i - 2);
                    instrs.add(new Mov(Operand.R10, calleeReturnAddr));
                    return Operand.mem(Operand.R10, offset);
                } else {
                    int offset = normalize(callerReturnAddr - (i - 2));
                    return Operand.mem(Operand.RSP, offset);
                }
            case MULT_RET:
                return Operand.mem(Operand.RSP, normalize(callerReturnAddr));

        }
        assert false;
        return null;
    }
}