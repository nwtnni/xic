package assemble.instructions;

import assemble.*;

public class Cqo<A> extends Instr<A> {

    public static <T> Cqo<T> get() { return new Cqo<>(); }

    private Cqo() {}

    @Override
    public String toString() {
        return "cqo";
    }

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }
}
