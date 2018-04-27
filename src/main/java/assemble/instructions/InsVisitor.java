package assemble.instructions;

public abstract class InsVisitor<A, T> {

    public <L, R> T visit(BinOp<L, R, A> i) {
        return null;
    }

    public T visit(Call<A> i) {
        return null;
    }

    public <L, R> T visit(Cmp<L, R, A> i) {
        return null;
    }

    public T visit(Cqo<A> i) {
        return null;
    }

    public <S> T visit(DivMul<S, A> i) {
        return null;
    }

    public T visit(Jcc<A> i) {
        return null;
    }

    public T visit(Jmp<A> i) {
        return null;
    }

    public T visit(Label<A> l) {
        return null;
    }

//     public T visit(Lea i) {
//         return null;
//     }

//     public T visit(Mov i) {
//         return null;
//     }

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
