package assemble.instructions;

import assemble.*;

import util.Pair;

public class BinOp<L, R, T> extends Instr<T> {

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
    public L src;
    public R dest;

    /**
     * Creates a BinOp with operands.
     */
    private BinOp(Kind kind, L src, R dest) {
        this.kind = kind;
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %s", kind.opcode, src, dest);
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
    public static class AIR extends BinOp<Imm, Temp, Temp> {
        public AIR(Kind kind, Imm src, Temp dest) { super(kind, src, dest); }
    }

    /**
     * Abstract immediate source, memory destination addressing mode.
     */
    public static class AIM extends BinOp<Imm, Mem<Temp>, Temp> {
        public AIM(Kind kind, Imm src, Mem<Temp> dest) { super(kind, src, dest); }
    }

    /**
     * Abstract register source, memory destination addressing mode.
     */
    public static class ARM extends BinOp<Temp, Mem<Temp>, Temp> {
        public ARM(Kind kind, Temp src, Mem<Temp> dest) { super(kind, src, dest); }
    }

    /**
     * Abstract memory source, register destination addressing mode.
     */
    public static class AMR extends BinOp<Mem<Temp>, Temp, Temp> {
        public AMR(Kind kind, Mem<Temp> src, Temp dest) { super(kind, src, dest); }
    }

    /**
     * Abstract register source, register destination addressing mode.
     */
    public static class ARR extends BinOp<Temp, Temp, Temp> {
        public ARR(Kind kind, Temp src, Temp dest) { super(kind, src, dest); }
    }

    /*
     *
     * Assembly Instructions
     *
     */

    /**
     * Immediate source, register destination addressing mode.
     */
    public static class IR extends BinOp<Imm, Reg, Reg> {
        public IR(Kind kind, Imm src, Reg dest) { super(kind, src, dest); }
    }

    /**
     * Immediate source, memory destination addressing mode.
     */
    public static class IM extends BinOp<Imm, Mem<Reg>, Reg> {
        public IM(Kind kind, Imm src, Mem<Reg> dest) { super(kind, src, dest); }
    }

    /**
     * Register source, memory destination addressing mode.
     */
    public static class RM extends BinOp<Reg, Mem<Reg>, Reg> {
        public RM(Kind kind, Reg src, Mem<Reg> dest) { super(kind, src, dest); }
    }

    /**
     * Memory source, register destination addressing mode.
     */
    public static class MR extends BinOp<Mem<Reg>, Reg, Reg> {
        public MR(Kind kind, Mem<Reg> src, Reg dest) { super(kind, src, dest); }
    }

    /**
     * Register source, register destination addressing mode.
     */
    public static class RR extends BinOp<Reg, Reg, Reg> {
        public RR(Kind kind, Reg src, Reg dest) { super(kind, src, dest); }
    }
}
