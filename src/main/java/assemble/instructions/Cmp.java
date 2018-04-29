package assemble.instructions;

import assemble.*;

public abstract class Cmp<L, R, A> extends Instr<A> {

    public L left;
    public R right;

    private Cmp(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Left and right semantics are inherited from the IR so
     * left and right are flipped to comply with AT&T syntax.
     */
    @Override
    public String toString() {
        return String.format("cmpq %s, %s", right, left);
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract immediate source, register destination addressing mode.
     */
    public static class TIR extends Cmp<Imm, Temp, Temp> {
        public TIR(Imm left, Temp right) { super(left, right); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, memory destination addressing mode.
     */
    public static class TRM extends Cmp<Temp, Mem<Temp>, Temp> {
        public TRM(Temp left, Mem<Temp> right) { super(left, right); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source, register destination addressing mode.
     */
    public static class TMR extends Cmp<Mem<Temp>, Temp, Temp> {
        public TMR(Mem<Temp> left, Temp right) { super(left, right); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, register destination addressing mode.
     */
    public static class TRR extends Cmp<Temp, Temp, Temp> {
        public TRR(Temp left, Temp right) { super(left, right); }
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
    public static class RIR extends Cmp<Imm, Reg, Reg> {
        public RIR(Imm left, Reg right) { super(left, right); }
    }

    /**
     * Register source, memory destination addressing mode.
     */
    public static class RRM extends Cmp<Reg, Mem<Reg>, Reg> {
        public RRM(Reg left, Mem<Reg> right) { super(left, right); }
    }

    /**
     * Memory source, register destination addressing mode.
     */
    public static class RMR extends Cmp<Mem<Reg>, Reg, Reg> {
        public RMR(Mem<Reg> left, Reg right) { super(left, right); }
    }

    /**
     * Register source, register destination addressing mode.
     */
    public static class RRR extends Cmp<Reg, Reg, Reg> {
        public RRR(Reg left, Reg right) { super(left, right); }
    }
}
