package assemble;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;
import static assemble.instructions.InstrFactory.*;

import xic.XicInternalException;

public class TrivialAllocator extends InstrVisitor<Void> {

    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        TrivialAllocator allocator = new TrivialAllocator(unit);
        return allocator.allocate();
    }

    // Unallocated CompUnit
    private CompUnit<Temp> unit;

    // Running list of allocated assembly functions
    private CompUnit<Reg> allocated;

    // Running list of allocated function instructions
    private List<Instr<Reg>> instrs;

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
    private Mem<Reg> calleeReturnAddr;


    // Caller saved registers - not required for trivial allocation
    // private Operand r10;
    // private Operand r11;

    private TrivialAllocator(CompUnit<Temp> unit) {
        this.unit = unit;
        this.allocated = new CompUnit<>();
        this.tempStack = null;
        this.instrs = null;
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

    public Void visit(BinOp.TIR b) {
        // Move register into register
        Reg dest = load(b.dest, 0);
        instrs.add(new BinOp.RIR(b.kind, b.src, dest));
        store(b.dest, 0);
        return null;
    }

    public Void visit(BinOp.TIM b) {
        Mem<Reg> dest = load(b.dest);
        instrs.add(new BinOp.RIM(b.kind, b.src, dest));
        return null;
    }

    public Void visit(BinOp.TRM b) {
        // Move register into memory
        Mem<Reg> dest = load(b.dest);
        Reg src = load(b.src, 1);
        instrs.add(new BinOp.RRM(b.kind, src, dest));
        return null;
    }

    public Void visit(BinOp.TMR b) {
        // Move memory into register
        Mem<Reg> src = load(b.src);
        Reg dest = load(b.dest, 1);
        instrs.add(new BinOp.RMR(b.kind, src, dest));
        store(b.dest, 1);
        return null;
    }

    public Void visit(BinOp.TRR b) {
        // Move register into register
        Reg src = load(b.src, 0);
        Reg dest = load(b.dest, 1);
        instrs.add(new BinOp.RRR(b.kind, src, dest));
        store(b.dest, 1);
        return null;
    }

    /*
     * Call Visitor
     */

    public Void visit(Call.T c) {
        maxArgs = Math.max(maxArgs, c.numArgs);
        maxRets = Math.max(maxRets, c.numRet);
        instrs.add(new Call.R(c.name, c.numArgs, c.numRet));
        return null;
    }

    /*
     * Cmp Visitors
     */

    public Void visit(Cmp.TIR c) {
        // Compare immediate to register
        Reg right = load(c.right, 0);
        instrs.add(new Cmp.RIR(c.left, right));
        return null;
    }

    public Void visit(Cmp.TRM c) {
        // Compare register to memory
        Mem<Reg> right = load(c.right);
        Reg left = load(c.left, 1);
        instrs.add(new Cmp.RRM(left, right));
        return null;
    }

    public Void visit(Cmp.TMR c) {
        // Compare memory to register
        Mem<Reg> left = load(c.left);
        Reg right = load(c.right, 1);
        instrs.add(new Cmp.RMR(left, right));
        return null;
    }

    public Void visit(Cmp.TRR c) {
        // Compare register to register
        Reg left = load(c.left, 0);
        Reg right = load(c.right, 1);
        instrs.add(new Cmp.RRR(left, right));
        return null;
    }

    /*
     * Cqo Visitor
     */

    public Void visit(Cqo.T c) {
        instrs.add(new Cqo.R());
        return null;
    }

    /*
     * DivMul Visitors
     */

    public Void visit(DivMul.TR d) {
        // DivMul a register source
        Reg reg = load(d.src, 0);
        instrs.add(new DivMul.RR(d.kind, reg));
        return null;
    }

    public Void visit(DivMul.TM d) {
        Mem<Reg> reg = load(d.src);
        instrs.add(new DivMul.RM(d.kind, reg));
        return null;
    }

    /*
     * Jcc Visitor
     */

    public Void visit(Jcc.T j) {
        instrs.add(new Jcc.R(j.kind, j.target));
        return null;
    }

    /*
     * Jmp Visitor
     */

    public Void visit(Jmp.T j) {
        instrs.add(new Jmp.R(j.label));
        return null;
    }

    /*
     * Label Visitor
     */

    public Void visit(Label.T l) {
        instrs.add(new Label.R(l));
        return null;
    }

    /*
     * Lea Visitor
     */

    public Void visit(Lea.T l) {
        // Load into register destination
        Mem<Reg> src = load(l.src);
        Reg dest = load(l.dest, 1);   
        instrs.add(new Lea.R(src, dest));
        store(l.dest, 1);
        return null;
    }

    /*
     * Mov Visitors
     */

    public Void visit(Mov.TIR m) {
        // Move immediate into register
        Reg dest = load(m.dest, 0);
        instrs.add(new Mov.RIR(m.src, dest));
        store(m.dest, 0);
        return null;
    }

    public Void visit(Mov.TIM m) {
        Mem<Reg> dest = load(m.dest);
        instrs.add(new Mov.RIM(m.src, dest));
        return null;
    }

    public Void visit(Mov.TRM m) {
        // Move register into memory
        Reg src = load(m.src, 0);
        Mem<Reg> dest = load(m.dest);
        instrs.add(new Mov.RRM(src, dest));
        return null;
    }

    public Void visit(Mov.TMR m) {
        // Move memory into register
        Mem<Reg> src = load(m.src);
        Reg dest = load(m.dest, 1);
        instrs.add(new Mov.RMR(src, dest));
        store(m.dest, 1);
        return null;
    }

    public Void visit(Mov.TRR m) {
        Reg src = load(m.src, 0);
        Reg dest = load(m.dest, 1);
        instrs.add(new Mov.RRR(src, dest));
        store(m.dest, 1); 
        return null;
    }

    /*
     * Pop Visitors
     */

    public Void visit(Pop.TR p) {
        // Pop to register
        instrs.add(new Pop.RR(load(p.dest, 0)));
        return null;
    }

    public Void visit(Pop.TM p) {
        instrs.add(new Pop.RM(load(p.dest)));
        return null;
    }

    /*
     * Push Visitors
     */

    public Void visit(Push.TR p) {
        // Push from register
        instrs.add(new Push.RR(load(p.src, 0)));
        return null;
    }

    public Void visit(Push.TM p) {
        instrs.add(new Push.RM(load(p.src)));
        return null;
    }

    /*
     * Ret Visitor
     */

    public Void visit(Ret.T r) {
        instrs.add(new Ret.R());
        return null;
    }

    /*
     * Setcc Visitor
     */

    public Void visit(Setcc.T s) {
        instrs.add(new Setcc.R(s.kind));
        return null;
    }

    /*
     * Text Visitor
     */

    public Void visit(Text.T t) {
        instrs.add(new Text.R(t.text));
        return null;
    }

    private Reg load(Temp t, int index) {
        switch (t.kind) {

            // Allocate an ordinary temporary
            case TEMP:
                Mem<Reg> mem = loadTemp(t.name);
                Reg reg = (index == 0) ? Reg.R10 : Reg.R11;
                instrs.add(new Mov.RMR(mem, reg));
                return reg;

            // Get the fixed register
            case FIXED:
                return t.getRegister();
        }
        assert false;
        return null;
    }

    private void store(Temp t, int index) {
        Reg reg = (index == 0) ? Reg.R10 : Reg.R11;
        instrs.add(new Mov.RRM(reg, loadTemp(t.name)));
    }

    private Mem<Reg> load(Mem<Temp> mem) {
        switch (mem.kind) {
        case R:
            return Mem.of(load(mem.reg, 0));
        case RO:
            return Mem.of(load(mem.reg, 0), mem.offset);
        case RSO:
            return Mem.of(load(mem.reg, 0), mem.offset, mem.scale);
        case BRSO:
            Reg base = load(mem.base, 0);
            Reg reg = load(mem.reg, 1);
            return Mem.of(base, reg, mem.offset, mem.scale);
        }

        // Unreachable
        assert false;
        return null;
    }
}
