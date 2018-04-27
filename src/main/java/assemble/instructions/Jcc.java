package assemble.instructions;

import assemble.*;

import ir.IRLabel;

public abstract class Jcc<A> extends Instr<A> {

    public enum Kind {
        E   ("e"),
        NE  ("ne"),
        L   ("l"),
        G   ("g"),
        LE  ("le"),
        GE  ("ge"),
        Z   ("z");

        String cond;

        private Kind(String cond) { this.cond = cond; }
    }

    public Kind kind;
    public Label<A> target;

    private Jcc(Kind kind, IRLabel target) {
        this.kind = kind;
        this.target = Label.label(target);
    }

    @Override
    public String toString() {
        return String.format("j%s %s", kind.cond, target.name());
    }

    public static class T extends Jcc<Temp> {
        public T(Kind kind, IRLabel target) { super(kind, target); }
        public <T> T accept(InsVisitor<T> v) { return v.visit(this); }
    }
    
    public static class R extends Jcc<Reg> {
        public R(Kind kind, IRLabel target) { super(kind, target); }
    }
}
