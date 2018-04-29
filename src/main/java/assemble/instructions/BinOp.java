package assemble.instructions;

import assemble.*;

import util.Pair;

public abstract class BinOp<S, D, A> extends Instr<A> {

    public enum Kind {
        ADD     ("addq"),
        SUB     ("subq"),
        AND     ("andq"),
        OR      ("orq"),
        XOR     ("xorq");

        String opcode;
        private Kind (String s) { opcode = s; }
    }

    public Kind kind;
    public S src;
    public D dest;

    /**
     * Creates a BinOp with operands.
     */
    private BinOp(Kind kind, S src, D dest) {
        this.kind = kind;
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", kind.opcode, src, dest);
    }

    /*
     *
     * Temp Factory Methods
     *
     */

    public static BinOp<Imm, Temp, Temp> temp(Kind kind, Imm src, Temp dest) {
        return new TIR(kind, src, dest);
    }

    public static BinOp<Imm, Mem<Temp>, Temp> temp(Kind kind, Imm src, Mem<Temp> dest) {
        return new TIM(kind, src, dest);
    }

    public static BinOp<Temp, Mem<Temp>, Temp> temp(Kind kind, Temp src, Mem<Temp> dest) {
        return new TRM(kind, src, dest);
    }

    public static BinOp<Mem<Temp>, Temp, Temp> temp(Kind kind, Mem<Temp> src, Temp dest) {
        return new TMR(kind, src, dest);
    }

    public static BinOp<Temp, Temp, Temp> temp(Kind kind, Temp src, Temp dest) {
        return new TRR(kind, src, dest);
    }

    /*
     *
     * Reg Factory Methods
     *
     */

    public static BinOp<Imm, Reg, Reg> reg(Kind kind, Imm src, Reg dest) {
        return new RIR(kind, src, dest);
    }

    public static BinOp<Imm, Mem<Reg>, Reg> reg(Kind kind, Imm src, Mem<Reg> dest) {
        return new RIM(kind, src, dest);
    }

    public static BinOp<Reg, Mem<Reg>, Reg> reg(Kind kind, Reg src, Mem<Reg> dest) {
        return new RRM(kind, src, dest);
    }

    public static BinOp<Mem<Reg>, Reg, Reg> reg(Kind kind, Mem<Reg> src, Reg dest) {
        return new RMR(kind, src, dest);
    }

    public static BinOp<Reg, Reg, Reg> reg(Kind kind, Reg src, Reg dest) {
        return new RRR(kind, src, dest);
    }

    /*
     *
     * Abstract Assembly Instructions
     *
     */

    /**
     * Abstract immediate source, register destination addressing mode.
     */
    public static class TIR extends BinOp<Imm, Temp, Temp> {
        public TIR(Kind kind, Imm src, Temp dest) { super(kind, src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract immediate source, memory destination addressing mode.
     */
    public static class TIM extends BinOp<Imm, Mem<Temp>, Temp> {
        public TIM(Kind kind, Imm src, Mem<Temp> dest) { super(kind, src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, memory destination addressing mode.
     */
    public static class TRM extends BinOp<Temp, Mem<Temp>, Temp> {
        public TRM(Kind kind, Temp src, Mem<Temp> dest) { super(kind, src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract memory source, register destination addressing mode.
     */
    public static class TMR extends BinOp<Mem<Temp>, Temp, Temp> {
        public TMR(Kind kind, Mem<Temp> src, Temp dest) { super(kind, src, dest); }
        public <T> T accept(InstrVisitor<T> v) { return v.visit(this); }
    }

    /**
     * Abstract register source, register destination addressing mode.
     */
    public static class TRR extends BinOp<Temp, Temp, Temp> {
        public TRR(Kind kind, Temp src, Temp dest) { super(kind, src, dest); }
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
    public static class RIR extends BinOp<Imm, Reg, Reg> {
        public RIR(Kind kind, Imm src, Reg dest) { super(kind, src, dest); }
    }

    /**
     * Immediate source, memory destination addressing mode.
     */
    public static class RIM extends BinOp<Imm, Mem<Reg>, Reg> {
        public RIM(Kind kind, Imm src, Mem<Reg> dest) { super(kind, src, dest); }
    }

    /**
     * Register source, memory destination addressing mode.
     */
    public static class RRM extends BinOp<Reg, Mem<Reg>, Reg> {
        public RRM(Kind kind, Reg src, Mem<Reg> dest) { super(kind, src, dest); }
    }

    /**
     * Memory source, register destination addressing mode.
     */
    public static class RMR extends BinOp<Mem<Reg>, Reg, Reg> {
        public RMR(Kind kind, Mem<Reg> src, Reg dest) { super(kind, src, dest); }
    }

    /**
     * Register source, register destination addressing mode.
     */
    public static class RRR extends BinOp<Reg, Reg, Reg> {
        public RRR(Kind kind, Reg src, Reg dest) { super(kind, src, dest); }
    }
}
