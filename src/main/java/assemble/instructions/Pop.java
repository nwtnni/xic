package assemble.instructions;

import java.util.Set;

import assemble.*;

public abstract class Pop<D, A> extends Instr<A> {

    public D dest;

    private Pop(D dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "popq " + dest;
    }

    /**
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract register source addressing mode.
     */
    public static class TR extends Pop<Temp, Temp> {
        public TR(Temp dest) { super(dest); } 
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source addressing mode.
     */
    public static class TM extends Pop<Mem<Temp>, Temp> {
        public TM(Mem<Temp> dest) { super(dest); } 
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Register source addressing mode.
     */
    public static class RR extends Pop<Reg, Temp> {
        public RR(Reg dest) { super(dest); } 
    }

    /**
     * Memory source addressing mode.
     */
    public static class RM extends Pop<Mem<Reg>, Temp> {
        public RM(Mem<Reg> dest) { super(dest); } 
    }
}
