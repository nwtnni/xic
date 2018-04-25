package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class Lea extends Instr {
    public Temp destTemp;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Lea(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return Arrays.asList(String.format("leaq %s, %s", srcTemp, destTemp));
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("leaq %s, %s", src, dest));
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}