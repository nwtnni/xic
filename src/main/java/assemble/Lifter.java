package assemble;

import assemble.instructions.*;

public abstract class Lifter {

    /*
     * BinOp Lifters
     */

    public static BinOp.RIR lift(BinOp.TIR b, Reg dest) {
        return new BinOp.RIR(b.kind, b.src, dest);
    }

    public static BinOp.RIM lift(BinOp.TIM b, Mem<Reg> dest) {
        return new BinOp.RIM(b.kind, b.src, dest);
    }

    public static BinOp.RRM lift(BinOp.TRM b, Reg src, Mem<Reg> dest) {
        return new BinOp.RRM(b.kind, src, dest);
    }

    public static BinOp.RMR lift(BinOp.TMR b, Mem<Reg> src, Reg dest) {
        return new BinOp.RMR(b.kind, src, dest); 
    }

    public static BinOp.RRR lift(BinOp.TRR b, Reg src, Reg dest) {
        return new BinOp.RRR(b.kind, src, dest);
    }

    /*
     * Call Lifter
     */

    public static Call.R lift(Call.T c) {
        return new Call.R(c.name, c.numArgs, c.numRet);
    }

    /*
     * Cmp Lifters
     */

    public static Cmp.RIR lift(Cmp.TIR c, Reg right) {
        return new Cmp.RIR(c.left, right);
    }

    public static Cmp.RRM lift(Cmp.TRM c, Reg left, Mem<Reg> right) {
        return new Cmp.RRM(left, right);
    }

    public static Cmp.RMR lift(Cmp.TMR c, Mem<Reg> left, Reg right) {
        return new Cmp.RMR(left, right);
    }

    public static Cmp.RRR lift(Cmp.TRR c, Reg left, Reg right) {
        return new Cmp.RRR(left, right);
    }

    /*
     * Cqo Lifter
     */

    public static Cqo.R lift(Cqo.T c) {
        return new Cqo.R();
    }

    /*
     * DivMul Lifters
     */

    public static DivMul.RR lift(DivMul.TR d, Reg src) {
        return new DivMul.RR(d.kind, src);
    }

    public static DivMul.RM lift(DivMul.TM d, Mem<Reg> src) {
        return new DivMul.RM(d.kind, src);
    }

    /*
     * Jcc Lifter
     */

    public static Jcc.R lift(Jcc.T j) {
        return new Jcc.R(j.kind, j.target);
    }

    /*
     * Jmp Lifter
     */

    public static Jmp.R lift(Jmp.T j) {
        return null;
    }

    /*
     * Label Lifter
     */

    public static Label.R lift(Label.T l) {
        return new Label.R(l.name());
    }

    /*
     * Lea Lifter
     */

    public static Lea.R lift(Lea.T l, Mem<Reg> src, Reg dest) {
        return new Lea.R(src, dest);
    }

    /*
     * Mov Lifters
     */

    public static Mov.RIR lift(Mov.TIR m, Reg dest) {
        return new Mov.RIR(m.src, dest);
    }

    public static Mov.RIM lift(Mov.TIM m, Mem<Reg> dest) {
        return new Mov.RIM(m.src, dest);
    }

    public static Mov.RRM lift(Mov.TRM m, Reg src, Mem<Reg> dest) {
        return new Mov.RRM(src, dest);
    }

    public static Mov.RMR lift(Mov.TMR m, Mem<Reg> src, Reg dest) {
        return new Mov.RMR(src, dest);
    }

    public static Mov.RRR lift(Mov.TRR m, Reg src, Reg dest) {
        return new Mov.RRR(src, dest);
    }
    
    /*
     * Pop Lifters
     */

    public static Pop.RR lift(Pop.TR p, Reg dest) {
        return new Pop.RR(dest);
    }

    public static Pop.RM lift(Pop.TM p, Mem<Reg> dest) {
        return new Pop.RM(dest);
    }

    /*
     * Push Lifters
     */

    public static Push.RR lift(Push.TR p, Reg src) {
        return new Push.RR(src);
    }

    public static Push.RM lift(Push.TM p, Mem<Reg> src) {
        return new Push.RM(src);
    }

    /*
     * Ret Lifter
     */

    public static Ret.R lift(Ret.T r) {
        return new Ret.R();
    }

    /*
     * Setcc Lifter
     */

    public static Setcc.R lift(Setcc.T s, Reg dest) {
        return new Setcc.R(s.kind);
    }

    /*
     * Text Lifter
     */

    public static Text.R lift(Text.T t) {
        return new Text.R(t.text);
    }
}
