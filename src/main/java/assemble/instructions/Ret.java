package assemble.instructions;

import java.util.Arrays;
import java.util.List;

public class Ret extends Instr {
    public List<String> toAssembly() {
        return Arrays.asList("retq");
    }
}