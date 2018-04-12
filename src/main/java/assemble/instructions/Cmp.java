package assemble.instructions;

import java.util.List;
import java.util.Arrays;

import ir.*;
import assemble.*;

public class Cmp extends Instr {
    protected Temp destTemp;
    protected Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Cmp(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;
    }

    @Override
    public List<String> toAssembly() {
        return null;
    }
}