package assemble.instructions;

import assemble.*;

public abstract class Mov<S, D, A> extends Instr<A> {

    public S src;
    public D dest;

    private Mov(S src, D dest) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return String.format("movq %s, %s", src, dest);
    }

    @Override
    public boolean isMove() {
        return true;
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract immediate source, register destination addressing mode.
     */
    public static class TIR extends Mov<Imm, Temp, Temp> {
        public TIR(Imm src, Temp dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract immediate source, memory destination addressing mode.
     */
    public static class TIM extends Mov<Imm, Mem<Temp>, Temp> {
        public TIM(Imm src, Mem<Temp> dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, memory destination addressing mode.
     */
    public static class TRM extends Mov<Temp, Mem<Temp>, Temp> {
        public TRM(Temp src, Mem<Temp> dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source, register destination addressing mode.
     */
    public static class TMR extends Mov<Mem<Temp>, Temp, Temp> {
        public TMR(Mem<Temp> src, Temp dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, register destination addressing mode.
     */
    public static class TRR extends Mov<Temp, Temp, Temp> {
        public TRR(Temp src, Temp dest) { super(src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Immediate source, register destination addressing mode.
     */
    public static class RIR extends Mov<Imm, Reg, Reg> {
        public RIR(Imm src, Reg dest) { super(src, dest); }
    }

    /**
     * Immediate source, memory destination addressing mode.
     */
    public static class RIM extends Mov<Imm, Mem<Reg>, Reg> {
        public RIM(Imm src, Mem<Reg> dest) { super(src, dest); }
    }

    /**
     * Register source, memory destination addressing mode.
     */
    public static class RRM extends Mov<Reg, Mem<Reg>, Reg> {
        public RRM(Reg src, Mem<Reg> dest) { super(src, dest); }
    }

    /**
     * Memory source, register destination addressing mode.
     */
    public static class RMR extends Mov<Mem<Reg>, Reg, Reg> {
        public RMR(Mem<Reg> src, Reg dest) { super(src, dest); }
    }

    /**
     * Register source, register destination addressing mode.
     */
    public static class RRR extends Mov<Reg, Reg, Reg> {
        public RRR(Reg src, Reg dest) { super(src, dest); }
    }
}
