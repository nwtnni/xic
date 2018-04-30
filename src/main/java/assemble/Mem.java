package assemble;

import java.util.Set;
import java.util.HashSet;

/**
 * Represents a memory location operand for assembly instructions.
 */
public class Mem<T> {

    public enum Kind { R, RO, RSO, BRSO }

    public Kind kind;
    public T base;
    public T reg;
    public int offset;
    public int scale;

    /**
     * A memory access [reg]
     * In the form: (reg)
     */
    public static <A> Mem<A> of(A reg) {
        assert reg != null;
        return new Mem<>(Kind.R, null, reg, 0, 0);
    }

    /**
     * A memory access [reg + offset]
     * In the form: offset(reg)
     */
    public static <A> Mem<A> of(A reg, int offset) {
        assert reg != null;
        assert offset % Config.WORD_SIZE == 0;
        return new Mem<>(Kind.RO, null, reg, offset, 0);
    }

    /**
     * Returns the set of Temps used by this Mem.
     */
    public static Set<Temp> getTemps(Mem<Temp> mem) {
        Set<Temp> temps = new HashSet<>();
        switch (mem.kind) {
        case BRSO:
            temps.add(mem.base);
        default:
            temps.add(mem.reg);
        }
        return temps;
    }

    /*
     *
     * A memory access [(reg * scale) + offset]
     * In the form offset(reg,scale)
     * 
     * Scale must be 1, 2, 4 or 8
     */
    public static <A> Mem<A> of(A reg, int scale, int offset) {
        assert reg != null;
        assert offset % Config.WORD_SIZE == 0;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        return new Mem<>(Kind.RSO, null, reg, scale, offset);
    }

    /**
     * A memory access [(base) + (reg * scale) + offset]
     * In the form offset(base,reg,scale)
     * 
     * Scale must be 1, 2, 4 or 8
     */
    public static <A> Mem<A> of(A base, A reg, int scale, int offset) {
        assert base != null && reg != null;
        assert offset % Config.WORD_SIZE == 0;
        assert scale == 1 || scale == 2 || scale == 4 || scale == 8;
        return new Mem<>(Kind.BRSO, base, reg, scale, offset);
    }

    /**
     * Allocates the provided mem using the given register.
     */
    public static Mem<Reg> allocate(Mem<Temp> mem, Reg reg) {
        assert mem.kind != Kind.BRSO;
        return new Mem<>(mem.kind, null, reg, mem.scale, mem.offset);
    }

    /**
     * Allocates the provided mem using the given registers.
     */
    public static Mem<Reg> allocate(Mem<Temp> mem, Reg base, Reg reg) {
        assert mem.kind == Kind.BRSO;
        return new Mem<>(mem.kind, base, reg, mem.scale, mem.offset);
    }

    /**
     * Private constructor.
     */
    private Mem(Kind kind, T base, T reg, int scale, int offset) {
        this.kind = kind;
        this.base = base;
        this.reg = reg;
        this.scale = scale;
        this.offset = offset;
    }

    /**
     * Checks if this Mem is spilled onto the stack.
     */
    public boolean isSpill() {
        return false;
    }

    /**
     * Checks equality recursively depending on type.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Mem<?>)) return false;

        Mem<?> t = (Mem<?>) o;
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
