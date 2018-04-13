package assemble;

import interpret.Configuration;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Temp {

    protected static Temp arg(int i, boolean callee) {
        return new Temp(Kind.ARG, Configuration.ABSTRACT_ARG_PREFIX, i, callee);
    }

    protected static Temp ret(int i, boolean callee) {
        return new Temp(Kind.RET, Configuration.ABSTRACT_RET_PREFIX, i, callee);
    }

    protected static Temp temp(String name) {
        return new Temp(Kind.TEMP, name, 0, false);
    }

    public static Temp imm(long value) {
        return new Temp(Kind.IMM, null, value, false);
    }

    public static Temp mem(String name) {
        return new Temp(Kind.MEM, name, 0, false);
    }

    public final static Temp MULT_RET_ADDR = new Temp(Kind.MULT_RET, "RET_ADDR", 0, false);

    public enum Kind {
        ARG, RET, TEMP, IMM, MEM, MULT_RET;
    }

    public Kind kind;
    public String name;
    public long value;
    public boolean callee;

    private Temp(Kind kind, String name, long value, boolean callee) {
        this.kind = kind;
        this.name = name;
        this.value = value;
        this.callee = callee;
    }

    @Override
    public String toString() {
        switch(kind) {
            case TEMP:
                return name;
            case IMM:
                return "$" + Long.toString(value);
            case MEM:
                return "(" + name + ")";
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