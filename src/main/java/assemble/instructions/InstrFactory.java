package assemble.instructions;

import assemble.*;
import ir.*;

public abstract class InstrFactory {

    /*
     * BinOp Factory Methods
     */

    public static BinOp<Imm, Temp, Temp> binOpIR(BinOp.Kind kind, Imm src, Temp dest) {
        return new BinOp.TIR(kind, src, dest);
    }

    public static BinOp<Imm, Mem<Temp>, Temp> binOpIM(BinOp.Kind kind, Imm src, Mem<Temp> dest) {
        return new BinOp.TIM(kind, src, dest);
    }

    public static BinOp<Temp, Mem<Temp>, Temp> binOpRM(BinOp.Kind kind, Temp src, Mem<Temp> dest) {
        return new BinOp.TRM(kind, src, dest);
    }

    public static BinOp<Mem<Temp>, Temp> binOpMR(BinOp.Kind kind, Mem<Temp> src, Temp dest) {
        return new BinOp.TMR(kind, src, dest);
    }

    public static BinOp<Temp, Temp, Temp> binOpRR(BinOp.Kind kind, Temp dest, Temp src) {
        return new BinOp.TRR(kind, src, dest);
    }

    /*
     * Call Factory Method
     */

    public static Call<Temp> call(String name, int numArgs, int numRets) {
        return new Call.T(name, numArgs, numRets);
    }

    /*
     * Cmp Factory Methods
     */

    public static Cmp<Imm, Temp, Temp> cmpIR(Imm left, Temp right) {
        return new Cmp.TIR(left, right);
    }

    public static Cmp<Temp, Mem<Temp>, Temp> cmpRM(Temp left, Mem<Temp> right) {
        return new Cmp.TRM(left, right);
    }

    public static Cmp<Mem<Temp>, Temp, Temp> cmpMR(Mem<Temp> left, Temp right) {
        return new Cmp.TMR(left, right);
    }

    public static Cmp<Temp, Temp, Temp> cmpRR(Temp left, Temp right) {
        return new Cmp.TRR(left, right);
    }

    /*
     * Cqo Factory Method
     */

    public static Cqo<Temp> cqo() {
        return new Cqo.T();
    }

    /*
     * DivMul Factory Methods
     */

    public static DivMul<Temp> divMulR(DivMul.Kind kind, Temp src) {
        return new DivMul.TR(kind, src);
    }

    public static DivMul<Mem<Temp>> divMulM(DivMul.Kind kind, Mem<Temp> src) {
        return new DivMul.TM(kind, src);
    }

    /*
     * Jcc Factory Method
     */

    public static Jcc<Temp> jcc(Jcc.Kind kind, IRLabel target) {
        return new Jcc.T(kind, target);
    }

    /*
     * Jmp Factory Methods
     */

    public static Jmp<Temp> jmpFromLabel(Label<Temp> label) {
        return new Jmp.T(label);
    }

    public static Jmp<Temp> jmpFromIRJump(IRJump jump) {
        return new Jmp.T(jump);
    }

    /*
     * Label Factory Methods
     */

    public static Label<Temp> labelFromIRLabel(IRLabel l) {
        return new Label.T(l);
    }

    public static Label<Temp> labelFromFn(IRFuncDecl fn) {
        return new Label.T(fn.name() + ":");
    }

    public static Label<Temp> labelFromRet(IRFuncDecl fn) {
        return new Label.T("_RET_" + fn.name() + ":");
    }

    /*
     * Lea Factory Method
     */

    public static Lea<Temp> lea(Mem<Temp> src, Temp dest) {
        return new Lea.T(dest, src);    
    }

    /*
     * Mov Factory Methods
     */

    public static Mov<Imm, Temp, Temp> movIR(Imm src, Temp dest) {
        return new Mov.TIR(src, dest);
    }

    public static Mov<Imm, Mem<Temp>, Temp> movIM(Imm src, Mem<Temp> dest) {
        return new Mov.TIM(src, dest);
    }

    public static Mov<Temp, Mem<Temp>, Temp> movRM(Temp src, Mem<Temp> dest) {
        return new Mov.TRM(src, dest);
    }

    public static Mov<Mem<Temp>, Temp, Temp> movMR(Mem<Temp> src, Temp dest) {
        return new Mov.TMR(src, dest);
    }

    public static Mov<Temp, Temp, Temp> movRR(Temp src, Temp dest) {
        return new Mov.TRR(src, dest);
    }

    /*
     * Pop Factory Methods
     */

    public static Pop<Temp, Temp> popR(Temp dest) {
        return new Pop.TR(dest);
    }

    public static Pop<Mem<Temp>, Temp> popM(Mem<Temp> dest) {
        return new Pop.TM(dest);
    }
    
    /*
     * Push Factory Methods
     */

    public static Push<Temp, Temp> pushR(Temp src) {
        return new Push.TR(src);
    }

    public static Push<Mem<Temp>, Temp> pushM(Mem<Temp> src) {
        return new Push.TM(src);
    }

    /*
     * Ret Factory Method
     */

    public static Ret<Temp> ret() {
        return new Ret.T();
    }

    /*
     * Setcc Factory Method
     */

    public static Setcc<Temp> setcc(Setcc.Kind kind) {
        return new Setcc.T(kind); 
    }

    /*
     * Text Factory Method
     */

    public static Text<Temp> comment(String c) {
        return new Text.T("# " + c);
    }

    public static Text<Temp> text(String t) {
        return new Text.T(t);
    }

    public static Text<Temp> label(String l) {
        return new Text.T(l + ":");
    }
}
