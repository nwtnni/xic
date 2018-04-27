package assemble;

import java.util.HashSet;
import java.util.Set;

import static assemble.Temp.Kind.*;

/**
 * A wrapper class for all the possible operands for assembly instructions.
 */
public class Temp {

    // All the fixed registers
    public static final Temp RAX = Temp.fixed(Reg.RAX);
    public static final Temp RBX = Temp.fixed(Reg.RBX);
    public static final Temp RCX = Temp.fixed(Reg.RCX);
    public static final Temp RDX = Temp.fixed(Reg.RDX);
    public static final Temp RSI = Temp.fixed(Reg.RSI);
    public static final Temp RDI = Temp.fixed(Reg.RDI);
    public static final Temp RBP = Temp.fixed(Reg.RBP);
    public static final Temp RSP = Temp.fixed(Reg.RSP);
    public static final Temp R8  = Temp.fixed(Reg.R8);
    public static final Temp R9  = Temp.fixed(Reg.R9);
    public static final Temp R10 = Temp.fixed(Reg.R10);
    public static final Temp R11 = Temp.fixed(Reg.R11);
    public static final Temp R12 = Temp.fixed(Reg.R12);
    public static final Temp R13 = Temp.fixed(Reg.R13);
    public static final Temp R14 = Temp.fixed(Reg.R14);
    public static final Temp R15 = Temp.fixed(Reg.R15);

    /** Factory method for named temp. */
    protected static Temp temp(String name) {
        return new Temp(TEMP, name);
    }

    // For temp of a fixed register
    public static Temp fixed(Reg reg) {
        return new Temp(reg);
    }

    public enum Kind { TEMP, FIXED; }

    public Kind kind;

    /** The name of a TEMP */
    public String name;

    /** Register for fixed temp. */
    Reg reg;

    /** Constructor for a named temp. */
    private Temp(Kind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    /** Constructor for a fixed temp. */
    private Temp(Reg reg) {
        this.kind = FIXED;
        this.reg = reg;
    }

    public boolean isTemp() {
        return kind == TEMP;
    }

    public boolean isFixed() {
        return kind == FIXED;
    }

    /**
     * Gets the register associated with this fixed temp.
     * Requires temp is fixed.
     */
    public Reg getRegister() {
        assert isFixed();
        return reg;
    }

    @Override
    public int hashCode() {
        switch (kind) {
        case TEMP:
            return name.hashCode();
        case FIXED:
            return reg.hashCode();
        }

        assert false;
        return -1;
    }

    /**
     * Temp equality by name or register.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Temp)) return false;

        Temp t = (Temp) obj;
        if (isTemp() && t.isTemp()) {
            return name.equals(t.name);
        } else if (isFixed() && t.isFixed()) {
            return reg.equals(t.reg);
        }

        return false;
    }

    /**
     * toString() is used when outputting abstract assembly to dot files and to debug.
     */
    @Override
    public String toString() {
        switch(kind) {
        case TEMP:
            return name;
        case FIXED:
            return reg.toString();
        }

        assert false;
        return null;
    }
}
