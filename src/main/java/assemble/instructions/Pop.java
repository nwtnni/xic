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

    public List<String> toAssembly() {
        return Arrays.asList("popq " + operand.toString());
    }
}