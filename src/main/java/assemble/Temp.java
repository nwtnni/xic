package assemble;

import java.util.HashSet;
import java.util.Set;

import static assemble.Temp.Kind.*;

/**
 * A wrapper class for all the possible operands for assembly instructions.
 */
public class Temp {

    // All the fixed registers
    public static final Temp RAX = new Temp(Reg.RAX);
    public static final Temp RBX = new Temp(Reg.RBX);
    public static final Temp RCX = new Temp(Reg.RCX);
    public static final Temp RDX = new Temp(Reg.RDX);
    public static final Temp RSI = new Temp(Reg.RSI);
    public static final Temp RDI = new Temp(Reg.RDI);
    public static final Temp RBP = new Temp(Reg.RBP);
    public static final Temp RSP = new Temp(Reg.RSP);
    public static final Temp R8  = new Temp(Reg.R8);
    public static final Temp R9  = new Temp(Reg.R9);
    public static final Temp R10 = new Temp(Reg.R10);
    public static final Temp R11 = new Temp(Reg.R11);
    public static final Temp R12 = new Temp(Reg.R12);
    public static final Temp R13 = new Temp(Reg.R13);
    public static final Temp R14 = new Temp(Reg.R14);
    public static final Temp R15 = new Temp(Reg.R15);

    public enum Kind { TEMP, FIXED; }

    public Kind kind;

    /** The name of a TEMP */
    public String name;

    /** Register for fixed temp. */
    Reg reg;

    /** Constructor for a named temp. */
    public Temp(String name) {
        this.kind = Kind.TEMP;
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
