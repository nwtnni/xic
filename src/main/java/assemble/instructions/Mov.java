package assemble.instructions;

import assemble.*;

public class Mov extends Instr {
    public Temp destTemp;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Mov(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;

        this.use = this.srcTemp.getTemps();
        this.def = this.destTemp.getTemps();
    }

    public Mov(Operand dest, Operand src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toAbstractAssembly() {
        if (srcTemp == null && destTemp == null) {
            return toAssembly();
        } else {
            return String.format("movq %s, %s", srcTemp, destTemp);
        }
    }

    @Override
    public String toAssembly() {
        return String.format("movq %s, %s", src, dest);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}