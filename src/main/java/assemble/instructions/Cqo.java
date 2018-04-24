package assemble.instructions;

import java.util.Arrays;
import java.util.List;

public class Cqo extends Instr {
    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList("cqo");
    }
}