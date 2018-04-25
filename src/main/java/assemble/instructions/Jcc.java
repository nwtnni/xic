package assemble.instructions;

import java.util.List;
import java.util.Arrays;

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
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("j%s %s", kind.cond, target.name()));
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}