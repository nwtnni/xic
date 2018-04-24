package assemble;

import static assemble.Temp.Kind.*;

import interpret.Configuration;

/**
 * A wrapper class for all the possible operands for assembly instructions.
 */
public class Temp {

    // Special temps for multiple return address
    public final static Temp CALLEE_RET_ADDR = new Temp(MULT_RET, "CALLEE_RET_ADDR", 0, true);
    public final static Temp CALLER_RET_ADDR = new Temp(MULT_RET, "CALLER_RET_ADDR", 0, false);

    /* Temp factory methods. ------------------------------------------ */

    // Special temp for arguments
    protected static Temp arg(int i, boolean callee) {
        return new Temp(ARG, Configuration.ABSTRACT_ARG_PREFIX, i, callee);
    }

    // Special temp for returns
    protected static Temp ret(int i, boolean callee) {
        return new Temp(RET, Configuration.ABSTRACT_RET_PREFIX, i, callee);
    }

    protected static Temp temp(String name) {
        return new Temp(TEMP, name, -1, false);
    }

    public static Temp imm(long value) {
        return new Temp(value);
    }

    // 3 kinds of memory addressing modes

    public static Temp mem(Temp b) {
        assert b != null && b.isTemp();
        return new Temp(MEM, b, null, 0, 1);
    }

    public static Temp mem(Temp b, int off) {
        assert b != null && b.isTemp();
        return new Temp(MEMBR, b, null, off, 1);
    }

    public static Temp mem(Temp b, Temp r, int off, int scale) {
        assert b != null && b.isTemp();
        assert r != null && r.isTemp();
        return new Temp(MEMSBR, b, r, off, scale);
    }

    // Constructor for temp of a fixed register
    public static Temp fixed(Operand reg) {
        assert reg.isReg();
        return new Temp(reg);
    }

    /* Temp implementation -------------------------------------------- */

    public enum Kind {
        ARG, RET, TEMP, IMM, MEM, MEMBR, MEMSBR, MULT_RET, FIXED;
    }

    public Kind kind;

    /** The name of a TEMP */
    public String name;

    /** Value for IMM */
    public long value;

    /** Values for MEM, MEMBR and MEMSBR. */
    public Temp base;
    public Temp reg;
    public int scale;
    public int offset;

    /** Number for ARG and RET */
    public int number;

    /** Flag for ARG and RET temps for if it is used by the callee or caller. */
    public boolean callee;

    /** Register for fixed temp. */
    Operand register;

    /**  */
    private Temp(Kind kind, String name, int number, boolean callee) {
        this.kind = kind;
        this.name = name;
        this.number = number;
        this.callee = callee;
    }

    private Temp(long value) {
        this.kind = IMM;
        this.value = value;
    }

    private Temp(Kind kind, Temp base, Temp reg, int offset, int scale) {
        this.kind = kind;
        this.base = base;
        this.reg = reg;
        this.offset = offset;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        this.scale = scale;
    }

    private Temp(Operand reg) {
        this.kind = FIXED;
        this.register = reg;
    }

    public boolean isImm() {
        return kind == IMM;
    }

    public boolean isTemp() {
        return kind == TEMP || 
            (kind == ARG && number < 6) || 
            (kind == RET && number < 2) || 
            kind == FIXED;
    }

    public boolean isMem() {
        return !(isImm() || isTemp());
    }

    // All temps are memory addresses for trivial allocation
    public boolean trivialIsMem() {
        return !(isImm() || 
            (kind == ARG && number < 6) || 
            (kind == RET && number < 2) || 
            kind == FIXED);
    }

    @Override
    public String toString() {
        switch(kind) {
            case TEMP:
                return name;
            case IMM:
                return "$" + Long.toString(value);
            case MEM:
                return "(" + base + ")";
            case MEMBR:
                return String.format("%d(%s)", offset, base);
            case MEMSBR:
                return String.format("%d(%s, %s, %d)", offset, base, reg, scale);
            case ARG:
            case RET:
                return name + number;
            case MULT_RET:
                return name;
            case FIXED:
                return register.toString();
        }
        assert false;
        return null;
    }
}