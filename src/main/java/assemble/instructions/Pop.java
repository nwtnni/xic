package assemble.instructions;

import java.util.Set;

import assemble.*;

public class Pop extends Instr {
    public Operand operand;

    public Pop(Operand o) {
        assert (o.kind != Operand.Kind.MEM);
        operand = o;
        this.def = Set.of(Temp.fixed(o));
    }

    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return "popq " + operand;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}