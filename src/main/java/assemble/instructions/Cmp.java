package assemble.instructions;

import assemble.*;

public class Cmp extends Instr {
    public Temp leftTemp;
    public Temp rightTemp;

    public Operand left;
    public Operand right;

    public Cmp(Temp leftTemp, Temp rightTemp) {
        this.leftTemp = leftTemp;
        this.rightTemp = rightTemp;

        this.use = this.leftTemp.getTemps();
        this.use.addAll(this.rightTemp.getTemps());
    }

    @Override
    public String toAbstractAssembly() {
        return String.format("cmpq %s, %s", leftTemp, rightTemp);
    }

    @Override
    public String toAssembly() {
        return String.format("cmpq %s, %s", left, right);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}