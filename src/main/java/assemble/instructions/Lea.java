package assemble.instructions;

import assemble.*;

public abstract class Lea<A> extends Instr<A> {

    public A dest;
    public Mem<A> src;

    private Lea(A dest, Mem<A> src) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("leaq %s, %s", src, dest);
    }

    public static class T extends Lea<Temp> {
        public T(Temp dest, Mem<Temp> src) { super(dest, src); }
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Lea<Reg> {
        public R(Reg dest, Mem<Reg> src) { super(dest, src); }
    }
}
