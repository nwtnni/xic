package assemble.instructions;

import assemble.*;

public class Set extends Instr {
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
    public Operand dest;

    public Set(Kind kind) {
        this.kind = kind;
        this.dest = Operand.RAX;
    }

    @Override
    public String toAbstractAssembly() {
        return String.format("set%s %%al", kind.flag);
    }

    @Override
    public String toAssembly() {
        return String.format("set%s %%al", kind.flag);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}