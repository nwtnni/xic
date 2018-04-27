package assemble.instructions;

import assemble.*;

public class Cmp<L, R, A> extends Instr<A> {

    public L left;
    public R right;

    public Cmp(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("cmpq %s, %s", left, right);
    }

    @Override
    public <T> T accept(InsVisitor<T> v) {
        return v.visit(this);
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
    }

    /**
     * Abstract register source, memory destination addressing mode.
     */
    public static class TRM extends Cmp<Temp, Mem<Temp>, Temp> {
        public TRM(Temp left, Mem<Temp> right) { super(left, right); }
    }

    /**
     * Abstract memory source, register destination addressing mode.
     */
    public static class TMR extends Cmp<Mem<Temp>, Temp, Temp> {
        public TMR(Mem<Temp> left, Temp right) { super(left, right); }
    }

    /**
     * Abstract register source, register destination addressing mode.
     */
    public static class TRR extends Cmp<Temp, Temp, Temp> {
        public TRR(Temp left, Temp right) { super(left, right); }
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
