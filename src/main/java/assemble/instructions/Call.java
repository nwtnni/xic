package assemble.instructions;

import assemble.Temp;
import assemble.Reg;

public class Call<A> extends Instr<A> {

    public static <T> Call<T> of(String name, int numArgs, int numRet) {
        return new Call<>(name, numArgs, numRet);
    }

    public String name;
    public int numArgs;
    public int numRet;

    private Call(String name, int numArgs, int numRet) {
        this.name = name;
        this.numArgs = numArgs;
        this.numRet = numRet;
    }

    @Override
    public String toString() {
        return "callq " + name;
    }

    @Override
    public <T> T accept(InsVisitor<A, T> v) {
        return v.visit(this);
    }
}
