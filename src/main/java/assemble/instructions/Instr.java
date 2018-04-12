package assemble.instructions;

import java.util.List;

import ir.*;
import assemble.*;

public abstract class Instr {
    public abstract List<String> toAssembly();
}