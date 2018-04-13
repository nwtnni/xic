package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import assemble.*;

public class Cmp extends Instr {
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand left;
    public Operand right;

    public Cmp(Temp leftTemp, Temp rightTemp) {
        this.leftTemp = leftTemp;
        this.rightTemp = rightTemp;
    }

    @Override
    public List<String> toAbstractAssembly() {
        return Arrays.asList(String.format("cmpq %s, %s", leftTemp, rightTemp));
    }

    @Override
    public List<String> toAssembly() {
        return Arrays.asList(String.format("cmpq %s, %s", left, right));
    }
}