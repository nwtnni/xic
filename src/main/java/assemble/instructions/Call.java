package assemble.instructions;

import assemble.Temp;
import assemble.Reg;
import assemble.Mem;

public abstract class Call<N, A> extends Instr<A> {

    public N name;
    public int numArgs;
    public int numRet;

    private Call(N name, int numArgs, int numRet) {
        this.name = name;
        this.numArgs = numArgs;
        this.numRet = numRet;
    }

    @Override
    public String toString() {
        if (name instanceof Temp || name instanceof Reg || name instanceof Mem<?>) {
            return "callq * " + name;
        } else {
            return "callq " + name;
        }
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Call of a label.
     */
    public static class TL extends Call<String, Temp> {
        public TL(String name, int args, int rets) { super(name, args, rets); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Call of a temp.
     */
    public static class TR extends Call<Temp, Temp> {
        public TR(Temp name, int args, int rets) { super(name, args, rets); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }
    
    /**
     * Call of a memory address.
     */
    public static class TM extends Call<Mem<Temp>, Temp> {
        public TM(Mem<Temp> name, int args, int rets) { super(name, args, rets); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Call of a label.
     */
    public static class RL extends Call<String, Reg> {
        public RL(String name, int args, int rets) { super(name, args, rets); }
    }

    /**
     * Call of a temp.
     */
    public static class RR extends Call<Reg, Reg> {
        public RR(Reg name, int args, int rets) { super(name, args, rets); }
    }

    /**
     * Call of a memory address.
     */
    public static class RM extends Call<Mem<Reg>, Reg> {
        public RM(Mem<Reg> name, int args, int rets) { super(name, args, rets); }
    }
}
