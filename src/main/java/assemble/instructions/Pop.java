package assemble.instructions;

import java.util.Arrays;
import java.util.List;

import assemble.*;

public class Pop extends Instr {
    public Operand operand;

    public Pop(Operand o) {
        assert (o.kind != Operand.Kind.MEM);
        operand = o;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList("popq " + operand);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}