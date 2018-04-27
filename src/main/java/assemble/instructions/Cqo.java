package assemble.instructions;

import assemble.*;

public abstract class Cqo<A> extends Instr<A> {

    private Cqo() {}

    @Override
    public String toString() {
        return "cqo";
    }

    public static class T extends Cqo<Temp> {
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Cqo<Reg> {}
}
