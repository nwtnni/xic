package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class Mov extends Instr {
    public Temp destTemp;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Mov(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;
    }

    public Mov(Operand dest, Operand src) {
        this.dest = dest;
        this.src = src;
    }

    public Mov(Operand dest, Temp srcTemp) {
        this.dest = dest;
        this.srcTemp = srcTemp;
    }

    public Mov(Temp destTemp, Operand src) {
        this.destTemp = destTemp;
        this.src = src;
    }

    @Override
    public List<String> toAbstractAssembly() {
        if (srcTemp == null && destTemp == null) {
            return toAssembly();
        } else if (srcTemp == null) {
            return Arrays.asList(String.format("movq %s, %s", src, destTemp));
        } else if (destTemp == null) {
            return Arrays.asList(String.format("movq %s, %s", srcTemp, dest));
        } else {
            return Arrays.asList(String.format("movq %s, %s", srcTemp, destTemp));
        }
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("movq %s, %s", src, dest));
    }
}