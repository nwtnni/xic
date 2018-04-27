package assemble;

/**
 * Represents a memory location operand for assembly instructions.
 */
public class AbstractMem {

    private enum Kind { R, RO, RSO, BRSO }

    private Kind kind;
    private Temp base;
    private Temp reg;
    private int offset;
    private int scale;

    /**
     * A memory access [reg]
     * In the form: (reg)
     */
    public static AbstractMem mem(Temp reg) {
        assert reg != null;
        return new AbstractMem(Kind.R, null, reg, 0, 0);
    }

    /**
     * A memory access [reg + offset]
     * In the form: offset(reg)
     */
    public static AbstractMem mem(Temp reg, int offset) {
        assert reg != null;
        assert offset % Config.WORD_SIZE == 0;
        return new AbstractMem(Kind.RO, null, reg, offset, 0);
    }

    /**
     * A memory access [(reg * scale) + offset]
     * In the form offset(reg,scale)
     * 
     * Scale must be 1, 2, 4 or 8
     */
    public static AbstractMem mem(Temp reg, int offset, int scale) {
        assert reg != null;
        assert offset % Config.WORD_SIZE == 0;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        return new AbstractMem(Kind.RSO, null, reg, offset, scale);
    }

    /**
     * A memory access [(base) + (reg * scale) + offset]
     * In the form offset(base,reg,scale)
     * 
     * Scale must be 1, 2, 4 or 8
     */
    public static AbstractMem mem(Temp base, Temp reg, int offset, int scale) {
        assert base != null && reg != null;
        assert offset % Config.WORD_SIZE == 0;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        return new AbstractMem(Kind.BRSO, base, reg, offset, scale);
    }

    private AbstractMem(Kind kind, Temp base, Temp reg, int offset, int scale) {
        this.kind = kind;
        this.base = base;
        this.reg = reg;
        this.offset = offset;
        this.scale = scale;
    }

    /**
     * Checks equality recursively depending on type.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractMem)) return false;

        AbstractMem t = (AbstractMem) o;
        switch (kind) {
        case R:
            return reg.equals(t.reg);
        case RO:
            return reg.equals(t.reg) && offset == t.offset;
        case RSO:
            return reg.equals(t.reg) && scale == t.scale && offset == t.offset;
        case BRSO:
            return base.equals(t.base) && reg.equals(t.reg) && offset == t.offset && scale == t.scale;
        }

        assert false;
        return false;
    }

    /**
     * Compatibility with equals.
     */
    @Override
    public int hashCode() {
        switch (kind) {
        case R:
            return reg.hashCode();
        case RO:
            return 41 * reg.hashCode() ^ 59 * Integer.hashCode(offset);
        case RSO:
            return 41 * reg.hashCode() ^ 59 * Integer.hashCode(scale) ^ Integer.hashCode(offset);
        case BRSO:
            return 43 * reg.hashCode() ^ 61 * base.hashCode() ^
                71 * Integer.hashCode(offset) ^ 73 * Integer.hashCode(scale);
        }

        assert false;
        return 0;
    }

    /**
     * toString() is used when outputing abstract assembly to dot files and to debug.
     */
    @Override
    public String toString() {
        switch(kind) {
            case R:
                return "(" + reg + ")";
            case RO:
                return String.format("%d(%s)", offset, reg);
            case RSO:
                return String.format("%d(,%s,%d)", offset, reg, scale);
            case BRSO:
                return String.format("%d(%s, %s, %d)", offset, base, reg, scale);
        }
        assert false;
        return null;
    }
}
