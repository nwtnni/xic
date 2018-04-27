package assemble.instructions;

import assemble.Temp;
import assemble.Reg;

public class Call<A> extends Instr<A> {

    public String name;
    public int numArgs;
    public int numRet;

    public Call(String name, int numArgs, int numRet) {
        this.name = name;
        this.numArgs = numArgs;
        this.numRet = numRet;
    }

    public abstract class T extends Call<Temp> {
        public T(String name, int numArgs, int numRet) { super(name, numArgs, numRet); }
    }

    public abstract class R extends Call<Reg> {
        public R(String name, int numArgs, int numRet) { super(name, numArgs, numRet); }
    }

    @Override
    public String toString() {
        return "callq " + name;
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
    }
}
