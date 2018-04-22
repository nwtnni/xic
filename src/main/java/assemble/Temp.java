package assemble;

import static assemble.Temp.Kind.*;

import interpret.Configuration;

/**
 * A wrapper class for all the possible operands for assembly instructions.
 */
public class Temp {

    // Special temp for arguments
    protected static Temp arg(int i, boolean callee) {
        return new Temp(ARG, Configuration.ABSTRACT_ARG_PREFIX, i, callee);
    }

    // Special temp for returns
    protected static Temp ret(int i, boolean callee) {
        return new Temp(RET, Configuration.ABSTRACT_RET_PREFIX, i, callee);
    }

    protected static Temp temp(String name) {
        return new Temp(TEMP, name, 0, false);
    }

    public static Temp imm(long value) {
        return new Temp(value);
    }

    // 3 kinds of memory addressing modes

    public static Temp mem(Temp b) {
        return new Temp(MEM, b, null, 0, 1);
    }

    public static Temp mem(Temp b, int off) {
        return new Temp(MEMBR, b, null, off, 1);
    }

    public static Temp mem(Temp b, Temp r, int off, int scale) {
        return new Temp(MEMSBR, b, r, off, scale);
    }

    // Specicial temp for multiple return address
    public final static Temp MULT_RET_ADDR = new Temp(MULT_RET, "RET_ADDR", 0, false);

    public enum Kind {
        ARG, RET, TEMP, IMM, MEM, MEMBR, MEMSBR, MULT_RET;
    }

    public Kind kind;
    public String name;
    public Temp base;
    public Temp reg;
    public long value;
    public int scale;
    public int offset;
    public boolean callee;

    private Temp(Kind kind, String name, long value, boolean callee) {
        this.kind = kind;
        this.name = name;
        this.value = value;
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
        this.scale = scale;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
    }

    public boolean isImm() {
        return kind == IMM;
    }

    public boolean isTemp() {
        return (kind == ARG && value < 6) || (kind == RET && value < 2);
    }

    public boolean isMem() {
        return !(isImm() || isTemp());
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
                return name + value;
            case MULT_RET:
                return name;
        }
        assert false;
        return null;
    }
}