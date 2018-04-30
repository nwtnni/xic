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

public abstract class Allocator extends InstrVisitor<Boolean> {

    // Unallocated CompUnit
    protected CompUnit<Temp> unit;

    // Running list of allocated assembly functions
    protected CompUnit<Reg> allocated;

    // Running list of allocated function instructions
    protected List<Instr<Reg>> instrs;

    // Maximum number of args to a call in current function
    protected int maxArgs;

    // Maximum number of returns from a call in current function
    protected int maxRets;

    // Required for allocator classes to overload
    protected abstract Optional<Reg> allocate(Temp t, int index); 

    // Map of named temps to offset on stack
    protected Map<String, Integer> tempStack;

    // Number of temps on the stack - 1
    protected int tempCounter;

    /**
     * Load the memory address corresponding to this Mem operand.
     */
    protected Optional<Mem<Reg>> allocate(Mem<Temp> mem) {
        switch (mem.kind) {
        case R:
            Optional<Reg> reg = allocate(mem.reg, 0);
            return reg.map(r -> Mem.of(r));
        case RO:
            reg = allocate(mem.reg, 0);
            return reg.map(r -> Mem.of(r, mem.offset));
        case RSO:
            reg = allocate(mem.reg, 0);
            return reg.map(r -> Mem.of(r, mem.scale, mem.offset));
        case BRSO:
            Optional<Reg> base = allocate(mem.base, 0);
            reg = allocate(mem.reg, 2);
            return base.flatMap(b -> reg.map(r -> Mem.of(b, r, mem.scale, mem.offset)));
        }

        // Unreachable
        assert false;
        return null;
    }

    /**
     * Push a named temp to the stack.
     */
    protected Mem<Reg> storeTemp(String name) {
        if (tempStack.containsKey(name)) return loadTemp(name);

        tempStack.put(name, tempCounter++);
        return loadTemp(name);
    }

    /**
     * Get the mem operand to a temp on the stack.
     * Equivalent to -(i+1)*8(%rbp) where name -> i in the tempStack
     * +1 to offset for saved base pointer
     */
    protected Mem<Reg> loadTemp(String name) {
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
    protected int normalize(int i) {
        return i * Config.WORD_SIZE;
    }

    protected Allocator(CompUnit<Temp> unit) {
        this.unit = unit;
        this.allocated = new CompUnit<>();
        this.tempStack = null;
        this.instrs = null;
        this.tempCounter = 0;
        this.maxArgs = 0;
        this.maxRets = 0;
    }

    /*
     * BinOp Visitors
     */

    public Boolean visit(BinOp.TIR b) {
        // Move register into register
        Optional<Reg> dest = allocate(b.dest, 1);
        dest.ifPresent(d -> instrs.add(new BinOp.RIR(b.kind, b.src, d)));
        return dest.isPresent();
    }

    public Boolean visit(BinOp.TIM b) {
        Optional<Mem<Reg>> dest = allocate(b.dest);
        dest.ifPresent(d -> instrs.add(new BinOp.RIM(b.kind, b.src, d)));
        return dest.isPresent();
    }

    public Boolean visit(BinOp.TRM b) {
        // Move register into memory
        Optional<Mem<Reg>> dest = allocate(b.dest);
        Optional<Reg> src = allocate(b.src, 1);
        dest.ifPresent(d ->
            src.ifPresent(s ->
                instrs.add(new BinOp.RRM(b.kind, s, d))
            )
        );
        return dest.isPresent() && src.isPresent();
    }

    public Boolean visit(BinOp.TMR b) {
        // Move memory into register
        Optional<Mem<Reg>> src = allocate(b.src);
        Optional<Reg> dest = allocate(b.dest, 1);
        dest.ifPresent(d ->
            src.ifPresent(s ->
                instrs.add(new BinOp.RMR(b.kind, s, d))
            )
        );
        return dest.isPresent() && src.isPresent();
    }

    public Boolean visit(BinOp.TRR b) {
        // Move register into register
        Optional<Reg> src = allocate(b.src, 0);
        Optional<Reg> dest = allocate(b.dest, 1);
        dest.ifPresent(d ->
            src.ifPresent(s ->
                instrs.add(new BinOp.RRR(b.kind, s, d))
            )
        );
        return dest.isPresent() && src.isPresent();
    }

    /*
     * Call Visitor
     */

    public Boolean visit(Call.T c) {
        maxArgs = Math.max(maxArgs, c.numArgs);
        maxRets = Math.max(maxRets, c.numRet);
        instrs.add(new Call.R(c.name, c.numArgs, c.numRet));
        return true;
    }

    /*
     * Cmp Visitors
     */

    public Boolean visit(Cmp.TIR c) {
        // Compare immediate to register
        Optional<Reg> right = allocate(c.right, 1);
        right.ifPresent(r -> instrs.add(new Cmp.RIR(c.left, r)));
        return right.isPresent();
    }

    public Boolean visit(Cmp.TRM c) {
        // Compare register to memory
        Optional<Mem<Reg>> right = allocate(c.right);
        Optional<Reg> left = allocate(c.left, 1);
        right.ifPresent(r ->
            left.ifPresent(l ->
                instrs.add(new Cmp.RRM(l, r))
            )
        );
        return right.isPresent() && left.isPresent();
    }

    public Boolean visit(Cmp.TMR c) {
        // Compare memory to register
        Optional<Mem<Reg>> left = allocate(c.left);
        Optional<Reg> right = allocate(c.right, 1);
        right.ifPresent(r ->
            left.ifPresent(l ->
                instrs.add(new Cmp.RMR(l, r))
            )
        );
        return right.isPresent() && left.isPresent();
    }

    public Boolean visit(Cmp.TRR c) {
        // Compare register to register
        Optional<Reg> left = allocate(c.left, 0);
        Optional<Reg> right = allocate(c.right, 1);
        right.ifPresent(r ->
            left.ifPresent(l ->
                instrs.add(new Cmp.RRR(l, r))
            )
        );
        return right.isPresent() && left.isPresent();
    }

    /*
     * Cqo Visitor
     */

    public Boolean visit(Cqo.T c) {
        instrs.add(new Cqo.R());
        return true;
    }

    /*
     * DivMul Visitors
     */

    public Boolean visit(DivMul.TR d) {
        // DivMul a register source
        Optional<Reg> reg = allocate(d.src, 1);
        reg.ifPresent(r -> instrs.add(new DivMul.RR(d.kind, r)));
        return reg.isPresent();
    }

    public Boolean visit(DivMul.TM d) {
        Optional<Mem<Reg>> reg = allocate(d.src);
        reg.ifPresent(r -> instrs.add(new DivMul.RM(d.kind, r)));
        return reg.isPresent();
    }

    /*
     * Jcc Visitor
     */

    public Boolean visit(Jcc.T j) {
        instrs.add(new Jcc.R(j.kind, j.target));
        return true;
    }

    /*
     * Jmp Visitor
     */

    public Boolean visit(Jmp.T j) {
        instrs.add(new Jmp.R(j.label));
        return true;
    }

    /*
     * Label Visitor
     */

    public Boolean visit(Label.T l) {
        instrs.add(new Label.R(l));
        return true;
    }

    /*
     * Lea Visitor
     */

    public Boolean visit(Lea.T l) {
        // Load into register destination
        Optional<Mem<Reg>> src = allocate(l.src);
        Optional<Reg> dest = allocate(l.dest, 1);   
        src.ifPresent(s ->
            dest.ifPresent(d ->
                instrs.add(new Lea.R(s, d))
            )
        );
        return src.isPresent() && dest.isPresent();
    }

    /*
     * Mov Visitors
     */

    public Boolean visit(Mov.TIR m) {
        // Move immediate into register
        Optional<Reg> dest = allocate(m.dest, 1);
        dest.ifPresent(d -> instrs.add(new Mov.RIR(m.src, d)));
        return dest.isPresent();
    }

    public Boolean visit(Mov.TIM m) {
        Optional<Mem<Reg>> dest = allocate(m.dest);
        dest.ifPresent(d -> instrs.add(new Mov.RIM(m.src, d)));
        return dest.isPresent();
    }

    public Boolean visit(Mov.TRM m) {
        // Move register into memory
        Optional<Mem<Reg>> dest = allocate(m.dest);
        Optional<Reg> src = allocate(m.src, 1);
        src.ifPresent(s ->
            dest.ifPresent(d ->
                instrs.add(new Mov.RRM(s, d))
            )
        );
        return src.isPresent() && dest.isPresent();
    }

    public Boolean visit(Mov.TMR m) {
        // Move memory into register
        Optional<Mem<Reg>> src = allocate(m.src);
        Optional<Reg> dest = allocate(m.dest, 1);
        src.ifPresent(s ->
            dest.ifPresent(d ->
                instrs.add(new Mov.RMR(s, d))
            )
        );
        return src.isPresent() && dest.isPresent();
    }

    public Boolean visit(Mov.TRR m) {
        Optional<Reg> src = allocate(m.src, 0);
        Optional<Reg> dest = allocate(m.dest, 1);
        src.ifPresent(s ->
            dest.ifPresent(d ->
                instrs.add(new Mov.RRR(s, d))
            )
        );
        return src.isPresent() && dest.isPresent();
    }

    /*
     * Pop Visitors
     */

    public Boolean visit(Pop.TR p) {
        // Pop to register
        Optional<Reg> dest = allocate(p.dest, 1);
        dest.ifPresent(d -> instrs.add(new Pop.RR(d)));
        return dest.isPresent();
    }

    public Boolean visit(Pop.TM p) {
        Optional<Mem<Reg>> dest = allocate(p.dest);
        dest.ifPresent(d -> instrs.add(new Pop.RM(d)));
        return dest.isPresent();
    }

    /*
     * Push Visitors
     */

    public Boolean visit(Push.TR p) {
        // Push from register
        Optional<Reg> src = allocate(p.src, 1);
        src.ifPresent(s -> instrs.add(new Push.RR(s)));
        return src.isPresent();
    }

    public Boolean visit(Push.TM p) {
        Optional<Mem<Reg>> src = allocate(p.src);
        src.ifPresent(s -> instrs.add(new Push.RM(s)));
        return src.isPresent();
    }

    /*
     * Ret Visitor
     */

    public Boolean visit(Ret.T r) {
        instrs.add(new Ret.R());
        return true;
    }

    /*
     * Setcc Visitor
     */

    public Boolean visit(Setcc.T s) {
        instrs.add(new Setcc.R(s.kind));
        return true;
    }

    /*
     * Text Visitor
     */

    public Boolean visit(Text.T t) {
        instrs.add(new Text.R(t.text));
        return true;
    }
}
