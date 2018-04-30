package optimize.register;

import java.util.*;

import assemble.*;
import assemble.instructions.*;

public class TempReplacer extends InstrVisitor<Boolean> {

    /**
     * Coalesces temporaries in a FuncDecl fn given the analysis in ColorGraph cg.
     */
    public static FuncDecl<Temp> replaceAll(FuncDecl<Temp> fn, ColorGraph cg) {
        TempReplacer t = new TempReplacer(cg);
        List<Instr<Temp>> instrs = new ArrayList<>();

        for (int i = 0; i < fn.stmts.size(); i++) {
            Instr<Temp> ins = fn.stmts.get(i);
            if (t.replace(ins)) {
                instrs.add(ins);
            }
        }

        fn.stmts = instrs;
        return fn;
    }

    private ColorGraph cg;

    private TempReplacer(ColorGraph cg) {
        this.cg = cg;
    }

    /**
     * Coalesces temps inside the given instruction.
     *
     * Returns true if this instruction must be kept in the list.
     * Returns false if this instruction can be deleted as a result of coalescing.
     */
    private boolean replace(Instr<Temp> instr) {
        return instr.accept(this);
    }

    /**
     * Replaces [from] Temps inside [mem] with [to].
     */
    private void replace(Mem<Temp> mem) {
        switch (mem.kind) {
        case BRSO:
            mem.base = cg.getAlias(mem.base);
        default:
            mem.reg = cg.getAlias(mem.reg);
        }
    }

    /*
     * BinOp Visitors
     */

    @Override
    public Boolean visit(BinOp.TIR b) {
        b.dest = cg.getAlias(b.dest);
        return true;
    }

    @Override
    public Boolean visit(BinOp.TIM b) {
        replace(b.dest);
        return true;
    }

    @Override
    public Boolean visit(BinOp.TRM b) {
        b.src = cg.getAlias(b.src);
        replace(b.dest);
        return true;
    }

    @Override
    public Boolean visit(BinOp.TMR b) {
        replace(b.src);
        b.dest = cg.getAlias(b.dest);
        return true;
    }

    @Override
    public Boolean visit(BinOp.TRR b) {
        b.src = cg.getAlias(b.src);
        b.dest = cg.getAlias(b.dest);
        return true;
    }

    /*
     * Call Visitor
     */

    @Override
    public Boolean visit(Call.T c) {
        return true;
    }

    /*
     * Cmp Visitors
     */

    @Override
    public Boolean visit(Cmp.TIR c) {
        c.right = cg.getAlias(c.right);
        return true;
    }

    @Override
    public Boolean visit(Cmp.TRM c) {
        c.left = cg.getAlias(c.left);
        replace(c.right);
        return true;
    }

    @Override
    public Boolean visit(Cmp.TMR c) {
        replace(c.left);
        c.right = cg.getAlias(c.right);
        return true;
    }

    @Override
    public Boolean visit(Cmp.TRR c) {
        c.left = cg.getAlias(c.left);
        c.right = cg.getAlias(c.right);
        return true;
    }

    /*
     * Cqo Visitor
     */

    @Override
    public Boolean visit(Cqo.T c) {
        return true;
    }

    /*
     * DivMul Visitors
     */

    @Override
    public Boolean visit(DivMul.TR d) {
        d.src = cg.getAlias(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return true;
    }

    @Override
    public Boolean visit(DivMul.TM d) {
        replace(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return true;
    }

    /*
     * Jcc Visitor
     */

    @Override
    public Boolean visit(Jcc.T j) {
        return true;
    }

    /*
     * Jmp Visitor
     */

    @Override
    public Boolean visit(Jmp.T j) {
        return true;
    }

    /*
     * Label Visitor
     */

    @Override
    public Boolean visit(Label.T l) {
        return true;
    }

    /*
     * Lea Visitor
     */

    @Override
    public Boolean visit(Lea.T l) {
        replace(l.src);
        l.dest = cg.getAlias(l.dest);
        return true;
    }

    /*
     * Mov Visitors
     */

    @Override
    public Boolean visit(Mov.TIR m) {
        m.dest = cg.getAlias(m.dest);
        return true;
    }

    @Override
    public Boolean visit(Mov.TIM m) {
        replace(m.dest);
        return true;
    }

    @Override
    public Boolean visit(Mov.TRM m) {
        m.src = cg.getAlias(m.src);
        replace(m.dest);
        return true;
    }

    @Override
    public Boolean visit(Mov.TMR m) {
        replace(m.src);
        m.dest = cg.getAlias(m.dest);
        return true;
    }

    @Override
    public Boolean visit(Mov.TRR m) {
        m.src = cg.getAlias(m.src);
        m.dest = cg.getAlias(m.dest);
        return !m.src.equals(m.dest);
    }
    
    /*
     * Pop Visitors
     */

    @Override
    public Boolean visit(Pop.TR p) {
        p.dest = cg.getAlias(p.dest);
        return true;
    }

    @Override
    public Boolean visit(Pop.TM p) {
        replace(p.dest);
        return true;
    }

    /*
     * Push Visitors
     */

    @Override
    public Boolean visit(Push.TR p) {
        p.src = cg.getAlias(p.src);
        return true;
    }

    @Override
    public Boolean visit(Push.TM p) {
        replace(p.src);
        return true;
    }

    /*
     * Ret Visitor
     */

    @Override
    public Boolean visit(Ret.T r) {
        return true;
    }

    /*
     * Setcc Visitor
     */

    @Override
    public Boolean visit(Setcc.T s) {
        if (!cg.getAlias(s.dest).equals(s.dest)) {
            // Error in register allocation
            // Fixed register can't be alias
            assert false;
        }
        return true;
    }

    /*
     * Booleanext Visitor
     */

    @Override
    public Boolean visit(Text.T t) {
        return true;
    }
}
