package assemble.instructions;

import assemble.*;

public class Cqo<A> extends Instr<A> {

    @Override
    public String toString() {
        return "cqo";
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }

    public static class T extends Cqo<Temp> {}

    public static class R extends Cqo<Reg> {}

}
