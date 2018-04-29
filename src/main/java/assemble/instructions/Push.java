package assemble.instructions;

import java.util.Set;

import assemble.*;

public abstract class Push<S, A> extends Instr<A> {

    public S src;

    private Push(S src) {
        this.src = src;
    }
    
    @Override
    public String toString() {
        return "pushq " + src;
    }

    /**
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract register source addressing mode.
     */
    public static class TR extends Push<Temp, Temp> {
        public TR(Temp src) { super(src); } 
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source addressing mode.
     */
    public static class TM extends Push<Mem<Temp>, Temp> {
        public TM(Mem<Temp> src) { super(src); } 
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
    public static class RR extends Push<Reg, Reg> {
        public RR(Reg src) { super(src); } 
    }

    /**
     * Memory source addressing mode.
     */
    public static class RM extends Push<Mem<Reg>, Reg> {
        public RM(Mem<Reg> src) { super(src); } 
    }
}
