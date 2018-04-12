package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class Jmp extends Instr {

    public Temp target;

    public Jmp(Temp target) {
        this.target = target;
    }

    @Override
    public List<String> toAssembly() {
        return null;
    }
}