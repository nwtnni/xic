package assemble.instructions;

import java.util.List;
import java.util.Arrays;

public class Jmp extends Instr {

    // Only support translating jumps to labels for now
    public String target;

    public Jmp(String target) {
        this.target = target;
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList("jmp " + target);
    }
}