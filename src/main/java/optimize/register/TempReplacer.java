package optimize.register;

import assemble.*;
import assemble.instructions.*;

public class TempReplacer extends InstrVisitor<Void> {

    public static void replace(Instr<Temp> instr, Temp from, Temp to) {
        instr.accept(new TempReplacer(from, to));
    }

    private TempReplacer(Temp from, Temp to) {
        this.from = from; 
        this.to = to;
    }

    private Temp from;
    private Temp to;

    /*
     * BinOp Visitors
     */

    public Void visit(BinOp.TIR b) {
        if (b.dest.equals(from)) b.dest = to;
        return null;
    }

    public Void visit(BinOp.TIM b) {
        Mem.replace(b.dest, from, to);
        return null;
    }

    public Void visit(BinOp.TRM b) {
        if (b.src.equals(from)) b.src = to;
        Mem.replace(b.dest, from, to);
        return null;
    }

    public Void visit(BinOp.TMR b) {
        Mem.replace(b.src, from, to);
        if (b.dest.equals(from)) b.dest = to;
        return null;
    }

    public Void visit(BinOp.TRR b) {
        if (b.src.equals(from)) b.src = to;
        if (b.dest.equals(from)) b.dest = to;
        return null;
    }

    /*
     * Call Visitor
     */

    public Void visit(Call.T c) {
        return null;
    }

    /*
     * Cmp Visitors
     */

    public Void visit(Cmp.TIR c) {
        if (c.right.equals(from)) c.right = to;
        return null;
    }

    public Void visit(Cmp.TRM c) {
        if (c.left.equals(from)) c.left = to;
        Mem.replace(c.right, from, to);
        return null;
    }

    public Void visit(Cmp.TMR c) {
        Mem.replace(c.left, from, to);
        if (c.right.equals(from)) c.right = to;
        return null;
    }

    public Void visit(Cmp.TRR c) {
        if (c.left.equals(from)) c.left = to;
        if (c.right.equals(from)) c.right = to;
        return null;
    }

    /*
     * Cqo Visitor
     */

    public Void visit(Cqo.T c) {
        return null;
    }

    /*
     * DivMul Visitors
     */

    public Void visit(DivMul.TR d) {
        if (d.src.equals(from)) d.src = to;
        return null;
    }

    public Void visit(DivMul.TM d) {
        Mem.replace(d.src, from, to);
        return null;
    }

    /*
     * Jcc Visitor
     */

    public Void visit(Jcc.T j) {
        return null;
    }

    /*
     * Jmp Visitor
     */

    public Void visit(Jmp.T j) {
        return null;
    }

    /*
     * Label Visitor
     */

    public Void visit(Label.T l) {
        return null;
    }

    /*
     * Lea Visitor
     */

    public Void visit(Lea.T l) {
        Mem.replace(l.src, from, to);
        if (l.dest.equals(from)) l.dest = to;
        return null;
    }

    /*
     * Mov Visitors
     */

    public <L, R> Void visit(Mov.TIR m) {
        if (m.dest.equals(from)) m.dest = to;
        return null;
    }

    public <L, R> Void visit(Mov.TIM m) {
        Mem.replace(m.dest, from, to);
        return null;
    }

    public <L, R> Void visit(Mov.TRM m) {
        if (m.src.equals(from)) m.src = to;
        Mem.replace(m.dest, from, to);
        return null;
    }

    public <L, R> Void visit(Mov.TMR m) {
        Mem.replace(m.src, from, to);
        if (m.dest.equals(from)) m.dest = to;
        return null;
    }

    public <L, R> Void visit(Mov.TRR m) {
        if (m.src.equals(from)) m.src = to;
        if (m.dest.equals(from)) m.dest = to;
        return null;
    }
    
    /*
     * Pop Visitors
     */

    public Void visit(Pop.TR p) {
        if (p.dest.equals(from)) p.dest = to;
        return null;
    }

    public Void visit(Pop.TM p) {
        Mem.replace(p.dest, from, to);
        return null;
    }

    /*
     * Push Visitors
     */

    public Void visit(Push.TR p) {
        if (p.src.equals(from)) p.src = to;
        return null;
    }

    public Void visit(Push.TM p) {
        Mem.replace(p.src, from, to);
        return null;
    }

    /*
     * Ret Visitor
     */

    public Void visit(Ret.T r) {
        return null;
    }

    /*
     * Setcc Visitor
     */

    public Void visit(Setcc.T s) {
        if (s.dest.equals(from)) {

            // Bad register allocation
            assert false; 
            s.dest = to;

        }
        return null;
    }

    /*
     * Voidext Visitor
     */

    public Void visit(Text.T t) {
        return null;
    }
}
