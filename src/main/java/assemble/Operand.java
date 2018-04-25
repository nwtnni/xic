package assemble;

import static assemble.Operand.Kind.*;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Operand {

    public enum Kind {
        RAX ("%rax"),
        RBX ("%rbx"),
        RCX ("%rcx"),
        RDX ("%rdx"),
        RSI ("%rsi"),
        RDI ("%rdi"),
        RBP ("%rbp"),
        RSP ("%rsp"),
        R8  ("%r8"),
        R9  ("%r9"),
        R10 ("%r10"),
        R11 ("%r11"),
        R12 ("%r12"),
        R13 ("%r13"),
        R14 ("%r14"),
        R15 ("%r15"),
        MEM ("MEM"),
        IMM ("IMM");

        public String name;

        private Kind(String name) {
            this.name = name;
        }
    }

    public static final Operand RAX = Operand.reg(Kind.RAX);
    public static final Operand RBX = Operand.reg(Kind.RBX);
    public static final Operand RCX = Operand.reg(Kind.RCX);
    public static final Operand RDX = Operand.reg(Kind.RDX);
    public static final Operand RSI = Operand.reg(Kind.RSI);
    public static final Operand RDI = Operand.reg(Kind.RDI);
    public static final Operand RBP = Operand.reg(Kind.RBP);
    public static final Operand RSP = Operand.reg(Kind.RSP);
    public static final Operand R8  = Operand.reg(Kind.R8);
    public static final Operand R9  = Operand.reg(Kind.R9);
    public static final Operand R10 = Operand.reg(Kind.R10);
    public static final Operand R11 = Operand.reg(Kind.R11);
    public static final Operand R12 = Operand.reg(Kind.R12);
    public static final Operand R13 = Operand.reg(Kind.R13);
    public static final Operand R14 = Operand.reg(Kind.R14);
    public static final Operand R15 = Operand.reg(Kind.R15);


    /**
     * One of the 64-bit registers.
     */
    private static Operand reg(Kind kind) {
        assert (kind != IMM && kind != MEM);
        return new Operand(kind, null, 0);
    }

    /**
     * An arbitrary string that from an immediate:
     * mem must be a valid memory access
     */
    public static Operand mem(int imm) {
        return new Operand(MEM, String.format("(%d)", imm), 0);
    }

    /**
     * A memory access [base]
     * In the form: (base)
     */
    public static Operand mem(Operand base) {
        assert (base.kind != IMM && base.kind != MEM);
        String mem = String.format("(%s)", base);
        return new Operand(MEM, mem, 0);
    }

    /**
     * A memory access [base + offset]
     * In the form: offset(base)
     */
    public static Operand mem(Operand base, int offset) {
        assert (base.kind != IMM && base.kind != MEM);
        String mem = String.format("%d(%s)", offset, base);
        return new Operand(MEM, mem, 0);
    }

    /**
     * A memory access [scale*reg + offset]
     * In the form offset(base,scale)
     * 
     * scale must be 1, 2, 4 or 8
     */
    public static Operand mem(Operand reg, int offset, int scale) {
        assert (reg.kind != IMM && reg.kind != MEM);
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        String mem = String.format("%d(,%s,%d)", offset, reg, scale);
        return new Operand(MEM, mem, 0);
    }

    /**
     * A memory access [base + scale*reg + offset]
     * In the form offset(base,reg,scale)
     * 
     * scale must be 1, 2, 4 or 8
     */
    public static Operand mem(Operand base, Operand reg, int offset, int scale) {
        assert (base.kind != IMM && base.kind != MEM);
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        String mem = String.format("%d(%s,%s,%d)", offset, base, reg, scale);
        return new Operand(MEM, mem, 0);
    }

    /**
     * A immediate operand.
     */
    public static Operand imm(long value) {
        return new Operand(IMM, null, value);
    }

    public Kind kind;
    private String mem;
    private long value;

    private Operand(Kind kind, String mem, long value) {
        this.kind = kind;
        this.mem = mem;
        this.value = value;
    }

    public boolean isReg() {
        return kind != IMM && kind != MEM;
    }

    public boolean isImm() {
        return kind == IMM;
    }

    public boolean isMem() {
        return kind == MEM;
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        if (kind == MEM) {
            return mem;
        } else if (kind == IMM) {
            return "$" + Long.toString(value);
        }
        return kind.name;
    }
}