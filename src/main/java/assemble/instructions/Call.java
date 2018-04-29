package assemble.instructions;

import assemble.Temp;
import assemble.Reg;

public abstract class Call<A> extends Instr<A> {

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

    public static class T extends Call<Temp> {
        public T(String name, int args, int rets) { super(name, args, rets); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    public static class R extends Call<Reg> {
        public R(String name, int args, int rets) { super(name, args, rets); }
    }
}
