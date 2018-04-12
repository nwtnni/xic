package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class Lea extends Instr {
    protected Temp destTemp;
    protected Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Lea(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return Arrays.asList(String.format("leaq %s %s", srcTemp.toString(), destTemp.toString()));
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("leaq %s %s", src.toString(), dest.toString()));
    }
}