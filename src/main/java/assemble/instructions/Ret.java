package assemble.instructions;

import assemble.*;

public abstract class Ret<A> extends Instr<A> {

    @Override
    public String toString() {
        return "retq";
    }

    public static class T extends Ret<Temp> {
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Ret<Reg> {}
}
