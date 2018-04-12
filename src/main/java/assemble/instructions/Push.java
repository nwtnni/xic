package assemble.instructions;

import java.util.Arrays;
import java.util.List;

import assemble.*;

public class Push extends Instr {
    public Operand operand;

    public Push(Operand o) {
        assert (o.kind != Operand.Kind.MEM);
        operand = o;
    }
    
    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList("pushq " + operand.toString());
    }
}