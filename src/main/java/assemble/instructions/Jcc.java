package assemble.instructions;

import ir.IRLabel;

public class Jcc extends Instr {

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
    public Label target;

    public Jcc(Kind kind, IRLabel target) {
        this.kind = kind;
        this.target = Label.label(target);
    }


    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return String.format("j%s %s", kind.cond, target.name());
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}