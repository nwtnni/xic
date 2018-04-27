package assemble.instructions;

import assemble.*;

public abstract class InsVisitor<T> {

    /*
     *
     * BinOp visitors
     *
     */

    public T visit(BinOp.TIR b) {
        return null;
    }

    public T visit(BinOp.TIM b) {
        return null;
    }

    public T visit(BinOp.TRM b) {
        return null;
    }

    public T visit(BinOp.TMR b) {
        return null;
    }

    public T visit(BinOp.TRR i) {
        return null;
    }

    /*
     *
     * Call visitor
     *
     */

    public T visit(Call.T b) {
        return null;
    }

    /*
     *
     * Cmp visitors
     *
     */

    public T visit(Cmp.TIR c) {
        return null;
    }

    public T visit(Cmp.TRM c) {
        return null;
    }

    public T visit(Cmp.TMR c) {
        return null;
    }

    public T visit(Cmp.TRR c) {
        return null;
    }

    /*
     *
     * Cqo visitor
     *
     */

    public T visit(Cqo.T i) {
        return null;
    }

    /*
     *
     * DivMul visitors
     *
     */

    public T visit(DivMul.TR d) {
        return null;
    }

    public T visit(DivMul.TM d) {
        return null;
    }

    /*
     *
     * Jcc visitor
     *
     */

    public T visit(Jcc.T j) {
        return null;
    }

    /*
     *
     * Jmp visitor
     *
     */

    public T visit(Jmp.T j) {
        return null;
    }

    /*
     *
     * Label visitor
     *
     */

    public T visit(Label.T l) {
        return null;
    }

    /*
     *
     * Lea visitor
     *
     */

    public T visit(Lea.T l) {
        return null;
    }

    /*
     *
     * Mov visitors
     *
     */

    public <L, R> T visit(Mov.TIR m) {
        return null;
    }

    public <L, R> T visit(Mov.TIM m) {
        return null;
    }

    public <L, R> T visit(Mov.TRM m) {
        return null;
    }

    public <L, R> T visit(Mov.TMR m) {
        return null;
    }

    public <L, R> T visit(Mov.TRR m) {
        return null;
    }

//     public T visit(Pop i) {
//         return null;
//     }

//     public T visit(Push i) {
//         return null;
//     }

//     public T visit(Ret i) {
//         return null;
//     }

//     public T visit(Setcc i) {
//         return null;
//     }

//     public T visit(Text i) {
//         return null;
//     }

}
