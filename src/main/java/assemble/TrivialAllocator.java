package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;
import static assemble.instructions.InstrFactory.*;

import xic.XicInternalException;

public class TrivialAllocator extends Allocator {

    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        TrivialAllocator allocator = new TrivialAllocator(unit);
        return allocator.allocate();
    }

    /**
     * TrivialAllocator spills everything onto the stack, reserving
     * %r9, %r10 and %r11 as shuttle registers.
     */
    @Override
    protected Optional<Reg> allocate(Temp t, int index) {
        switch (t.kind) {

            // Allocate an ordinary temporary
            case TEMP:
                boolean existing = tempStack.containsKey(t.name);
                Mem<Reg> mem = loadTemp(t.name);
                Reg reg = null;
                switch (index) {
                    case 0:
                        reg = Reg.R9;
                        break;
                    case 1:
                        reg = Reg.R10;
                        break;
                    case 2:
                        reg = Reg.R11;
                        break;
                    default:
                        assert false;
                }
                if (existing) instrs.add(new Mov.RMR(mem, reg));
                return Optional.of(reg);

            // Get the fixed register
            case FIXED:
                return Optional.of(t.getRegister());
        }
        assert false;
        return null;
    }

    /**
     * Places a Temp result back onto its stack location.
     */
    private void store(Temp t) {
        instrs.add(new Mov.RRM(Reg.R10, loadTemp(t.name)));
    }

    // Map of named temps to offset on stack
    private Map<String, Integer> tempStack;

    // Number of temps on the stack - 1
    private int tempCounter;

    // Number of words to subtract from base pointer to get location
    // in stack where multiple returns > 2 must be written by callee.
    private Mem<Reg> calleeReturnAddr;

    // Caller saved registers - not required for trivial allocation
    // private Operand r10;
    // private Operand r11;

    private TrivialAllocator(CompUnit<Temp> unit) {
        super(unit);
        this.tempStack = null;
        this.tempCounter = 0;
        this.maxArgs = 0;
        this.maxRets = 0;
        this.calleeReturnAddr = null;
    }

    /**
     * Push a named temp to the stack.
     */
    private Mem<Reg> storeTemp(String name) {
        if (tempStack.containsKey(name)) return loadTemp(name);

        tempStack.put(name, tempCounter++);
        return loadTemp(name);
    }

    /**
     * Get the mem operand to a temp on the stack.
     * Equivalent to -(i+1)*8(%rbp) where name -> i in the tempStack
     * +1 to offset for saved base pointer
     */
    private Mem<Reg> loadTemp(String name) {
        if (tempStack.containsKey(name)) {
            Mem<Temp> temp = Mem.of(Temp.RBP, -normalize(tempStack.get(name) + 1));
            return Mem.allocate(temp, Reg.RBP);
        } else {
            return storeTemp(name);
        }
    }

    /**
     * Multiply by the word size to get offset for a memory location.
     */
    private int normalize(int i) {
        return i * Config.WORD_SIZE;
    }

    /* Recursive descent visitors */

    private CompUnit<Reg> allocate() {
        for (FuncDecl<Temp> fn : unit.fns) {
            allocate(fn);
        }
        return allocated;
    }

    private void allocate(FuncDecl<Temp> fn) {
        FuncDecl<Reg> allocatedFn = new FuncDecl.R(fn);
        instrs = new ArrayList<>();
        tempStack = new HashMap<>();
        tempCounter = 0;
        maxArgs = 0;
        maxRets = 0;
        calleeReturnAddr = null;

        // Set CALLEE_RET_ADDR to a temp on stack
        if (fn.rets > 2) {
            calleeReturnAddr = storeTemp("CALLEE_RET_ADDR");
        }

        for (Instr<Temp> i : fn.stmts) {
            i.accept(this);
        }

        allocatedFn.stmts = instrs;
        allocated.fns.add(allocatedFn);

        // Calculate words to shift rsp, +1 to offset tempCounter TODO
        int rsp = tempCounter + maxArgs + maxRets;
        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Imm shift = new Imm(normalize(rsp));

        // Insert stack setup
        BinOp.RIR sub = new BinOp.RIR(Kind.SUB, shift, Reg.RSP);
        allocatedFn.prelude.set(allocatedFn.prelude.size() - 1, sub);

        // Insert stack teardown
        BinOp.RIR add = new BinOp.RIR(Kind.ADD, shift, Reg.RSP);
        allocatedFn.epilogue.set(2, add);
    }

    /*
     * BinOp Visitors
     */

    public Boolean visit(BinOp.TIR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    public Boolean visit(BinOp.TMR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    public Boolean visit(BinOp.TRR b) {
        super.visit(b);
        store(b.dest);
        return null;
    }

    /*
     * Lea Visitor
     */

    public Boolean visit(Lea.T l) {
        // Load into register destination
        super.visit(l);
        store(l.dest);
        return null;
    }

    /*
     * Mov Visitors
     */

    public Boolean visit(Mov.TIR m) {
        // Move immediate into register
        super.visit(m);
        store(m.dest);
        return null;
    }

    public Boolean visit(Mov.TMR m) {
        // Move memory into register
        super.visit(m);
        store(m.dest);
        return null;
    }

    public Boolean visit(Mov.TRR m) {
        super.visit(m);
        store(m.dest); 
        return null;
    }
}
