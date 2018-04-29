package optimize.register;

import assemble.*;
import assemble.instructions.*;

public class TempReplacer extends InstrVisitor<Void> {

    public TempReplacer(ColorGraph cg) {
        this.cg = cg;
    }

    private ColorGraph cg;

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
    public Void visit(BinOp.TIR b) {
        b.dest = cg.getAlias(b.dest);
        return null;
    }

    @Override
    public Void visit(BinOp.TIM b) {
        replace(b.dest);
        return null;
    }

    @Override
    public Void visit(BinOp.TRM b) {
        b.src = cg.getAlias(b.src);
        replace(b.dest);
        return null;
    }

    @Override
    public Void visit(BinOp.TMR b) {
        replace(b.src);
        b.dest = cg.getAlias(b.dest);
        return null;
    }

    @Override
    public Void visit(BinOp.TRR b) {
        b.src = cg.getAlias(b.src);
        b.dest = cg.getAlias(b.dest);
        return null;
    }

    /*
     * Call Visitor
     */

    @Override
    public Void visit(Call.T c) {
        return null;
    }

    /*
     * Cmp Visitors
     */

    @Override
    public Void visit(Cmp.TIR c) {
        c.right = cg.getAlias(c.right);
        return null;
    }

    @Override
    public Void visit(Cmp.TRM c) {
        c.left = cg.getAlias(c.left);
        replace(c.right);
        return null;
    }

    @Override
    public Void visit(Cmp.TMR c) {
        replace(c.left);
        c.right = cg.getAlias(c.right);
        return null;
    }

    @Override
    public Void visit(Cmp.TRR c) {
        c.left = cg.getAlias(c.left);
        c.right = cg.getAlias(c.right);
        return null;
    }

    /*
     * Cqo Visitor
     */

    @Override
    public Void visit(Cqo.T c) {
        return null;
    }

    /*
     * DivMul Visitors
     */

    @Override
    public Void visit(DivMul.TR d) {
        d.src = cg.getAlias(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return null;
    }

    @Override
    public Void visit(DivMul.TM d) {
        replace(d.src);
        if (!cg.getAlias(d.dest).equals(d.dest)) {
            // Error in register allocation
            // Can't alias fixed register dest
            assert false;
        }
        return null;
    }

    /*
     * Jcc Visitor
     */

    @Override
    public Void visit(Jcc.T j) {
        return null;
    }

    /*
     * Jmp Visitor
     */

    @Override
    public Void visit(Jmp.T j) {
        return null;
    }

    /*
     * Label Visitor
     */

    @Override
    public Void visit(Label.T l) {
        return null;
    }

    /*
     * Lea Visitor
     */

    @Override
    public Void visit(Lea.T l) {
        replace(l.src);
        l.dest = cg.getAlias(l.dest);
        return null;
    }

    /*
     * Mov Visitors
     */

    @Override
    public Void visit(Mov.TIR m) {
        m.dest = cg.getAlias(m.dest);
        return null;
    }

    @Override
    public Void visit(Mov.TIM m) {
        replace(m.dest);
        return null;
    }

    @Override
    public Void visit(Mov.TRM m) {
        m.src = cg.getAlias(m.src);
        replace(m.dest);
        return null;
    }

    @Override
    public Void visit(Mov.TMR m) {
        replace(m.src);
        m.dest = cg.getAlias(m.dest);
        return null;
    }

    @Override
    public Void visit(Mov.TRR m) {
        m.src = cg.getAlias(m.src);
        m.dest = cg.getAlias(m.dest);
        return null;
    }
    
    /*
     * Pop Visitors
     */

    @Override
    public Void visit(Pop.TR p) {
        p.dest = cg.getAlias(p.dest);
        return null;
    }

    @Override
    public Void visit(Pop.TM p) {
        replace(p.dest);
        return null;
    }

    /*
     * Push Visitors
     */

    @Override
    public Void visit(Push.TR p) {
        p.src = cg.getAlias(p.src);
        return null;
    }

    @Override
    public Void visit(Push.TM p) {
        replace(p.src);
        return null;
    }

    /*
     * Ret Visitor
     */

    @Override
    public Void visit(Ret.T r) {
        return null;
    }

    /*
     * Setcc Visitor
     */

    @Override
    public Void visit(Setcc.T s) {
        if (!cg.getAlias(s.dest).equals(s.dest)) {
            // Error in register allocation
            // Fixed register can't be alias
            assert false;
        }
        return null;
    }

    /*
     * Voidext Visitor
     */

    @Override
    public Void visit(Text.T t) {
        return null;
    }
}
