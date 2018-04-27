package assemble.instructions;

import java.util.Set;

import assemble.*;

public class Push extends Instr {
    public Operand operand;

    public Push(Operand o) {
        assert (o.kind != Operand.Kind.MEM);
        operand = o;
        this.use = Set.of(Temp.fixed(o));
    }
    
    @Override
    public String toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public String toAssembly() {
        return "pushq " + operand;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}