package assemble.instructions;

import assemble.*;

public abstract class Lea<A> extends Instr<A> {

    public Mem<A> src;
    public A dest;

    private Lea(Mem<A> src, A dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("leaq %s, %s", src, dest);
    }

    public static class T extends Lea<Temp> {
        public T(Mem<Temp> src, Temp dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Lea<Reg> {
        public R(Mem<Reg> src, Reg dest) { super(src, dest); }
    }
}
