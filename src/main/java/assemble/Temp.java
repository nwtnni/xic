package assemble;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Temp {

    protected static Temp arg(int i, boolean callee) {
        return new Temp(Kind.ARG, null, i, callee);
    }

    protected static Temp ret(int i, boolean callee) {
        return new Temp(Kind.RET, null, i, callee);
    }

    protected static Temp temp(String name) {
        return new Temp(Kind.TEMP, name, 0, false);
    }

    public static Temp imm(long value) {
        return new Temp(Kind.IMM, null, value, false);
    }

    public final static Temp MULT_RET_ADDR = new Temp(Kind.MULT_RET, null, 0, false);

    public enum Kind {
        ARG, RET, TEMP, IMM, MULT_RET;
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
        if (kind == Kind.TEMP) {
            return name;
        } else {
            return "$" + Long.toString(value);
        }
    }
}