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

    private int isMultipleReturn;

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
        this.isMultipleReturn = 0;
        this.calleeReturnAddr = null;
        this.callerReturnAddr = -1;
    }

    /**
     * Push a named temp to the stack.
     */
    private void pushTemp(String name) {
        tempStack.put(name, tempCounter++);
    }

    /**
     * Push an unnamed temp to the stack and return the mem operand.
     */
    private Operand pushTemp() {
        return Operand.mem(Operand.RBP, -normalize(++tempCounter));
    }

    /**
     * Get the mem operand to a temp on the stack.
     * Equivalent to -i*8(%rbp) where name -> i in the tempStack
     */
    private Operand getTemp(String name) {
        if (tempStack.containsKey(name)) {
            return Operand.mem(Operand.RBP, -normalize(tempStack.get(name)));
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
            allocate(fn);
        }
        return unit;
    }

    private void allocate(FuncDecl fn) {
        tempStack = new HashMap<>();
        tempCounter = 0;
        maxArgs = 0;
        maxRets = 0;
        isMultipleReturn = 0;
        calleeReturnAddr = null;

        // Store address to put multiple returns from arg0 to stack
        if (fn.rets > 2) {
            isMultipleReturn = 1;
            calleeReturnAddr = pushTemp();

            Operand dest = calleeReturnAddr;
            Operand src = Config.getArg(0);
            Mov mov = new Mov(dest, src);
            fn.prelude.set(4, mov);
        }

        for (Instr i : fn.stmts) {
            allocate(i);
        }

        // Calculate words to shift rsp, +1 to offset tempCounter
        int rsp = tempCounter + maxArgs + maxRets + 1;
        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Operand shift = Operand.imm(normalize(rsp));

        // Insert stack setup 
        BinOp sub = new BinOp(Kind.SUB, shift, Operand.RSP);
        fn.prelude.set(3, sub);

        // Insert stack teardown
        BinOp add = new BinOp(Kind.ADD, shift, Operand.RSP);
        fn.epilogue.set(2, add);
    }

    private Operand allocate(Instr ins) {
        return null;
    }

    private Operand allocate(Instr ins, Temp t) {
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
               
                String regex = String.format("(%s)(\\d)", Config.ABSTRACT_ARG_PREFIX);
                Matcher arg = Pattern.compile(regex).matcher(name);
                if (arg.find()) {
                    // Get arg, with offset for multiple return
                    int i = Integer.parseInt(arg.group(1)) + isMultipleReturn;
                    if (i < 6) {
                        return Config.getArg(i);
                    }

                    // -6 for regs, +1 to move above rbp
                    int offset = normalize(i - 6 + 1);
                    return Operand.mem(Operand.RBP, offset);
                } 
                
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

                if (!tempStack.containsKey(name)) {
                    pushTemp(name);
                }
                return getTemp(name);
        }
        assert false;
        return null;
    }
}