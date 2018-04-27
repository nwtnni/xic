package assemble.instructions;

import assemble.*;

public class Cqo extends Instr {
    public Cqo() {
    }

    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return "cqo";
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}