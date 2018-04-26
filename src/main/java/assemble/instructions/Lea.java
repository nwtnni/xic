package assemble.instructions;

import assemble.*;

public class Lea extends Instr {
    public Temp destTemp;
    public Temp srcTemp;

    public Operand dest;
    public Operand src;

    public Lea(Temp destTemp, Temp srcTemp) {
        this.destTemp = destTemp;
        this.srcTemp = srcTemp;
        
        this.use = this.srcTemp.getTemps();
        this.def = this.destTemp.getTemps();
    }

    @Override
    public String toAbstractAssembly() {
        return String.format("leaq %s, %s", srcTemp, destTemp);
    }

    @Override
    public String toAssembly() {
        return String.format("leaq %s, %s", src, dest);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}