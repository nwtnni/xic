package assemble.instructions;

import assemble.*;

public abstract class Setcc<A> extends Instr<A> {

    public enum Kind {
        EQ  ("e"),
        NEQ ("ne"),
        LT  ("l"),
        GT  ("g"),
        LEQ ("le"),
        GEQ ("ge");

        String flag;
        private Kind (String s) { flag = s; }
    }

    public Kind kind;
    public A dest;

    public Setcc(Kind kind, A dest) {
        this.kind = kind;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("set%s %%al", kind.flag);
    }

    public static class T extends Setcc<Temp> {
        public T(Kind kind) { super(kind, Temp.RAX); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Setcc<Reg> {
        public R(Kind kind) { super(kind, Reg.RAX); }
    }
}
