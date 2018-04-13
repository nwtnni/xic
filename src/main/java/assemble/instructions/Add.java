package assemble.instructions;

import java.util.Arrays;
import java.util.List;

import assemble.*;

public class Add extends Instr {

    public Operand dest;
    public Operand src;
    
    public Add(Operand dest, Operand src) {
        this.dest = dest;
        this.src = src;
    }
    
    @Override
    public List<String> toAbstractAssembly() {
        return toAssembly();
    }

    @Override
    public List<String> toAssembly() {
        String op = String.format("addq %s, %s", src, dest);
        return Arrays.asList(op);
    }
}