package assemble.instructions;

import assemble.*;

import ir.IRLabel;

public class Jcc<A> extends Instr<A> {

    public static <T> Jcc<T> of(Kind kind, IRLabel target) {
        return new Jcc<>(kind, target);
    }

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

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }
}
