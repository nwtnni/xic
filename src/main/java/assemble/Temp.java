package assemble;

/**
 * A wrapper class for all the possible operands for assembly
 * instructionsd.
 */
public class Temp {

    public static Temp temp(String name) {
        return new Temp(Kind.TEMP, name, 0);
    }

    public static Temp imm(long value) {
        return new Temp(Kind.IMM, null, value);
    }

    public enum Kind {
        TEMP ("TEMP"),
        IMM ("IMM");

        public String name;

        private Kind(String name) {
            this.name = name;
        }
    }

    public Kind kind;
    private String name;
    private long value;

    private Temp(Kind kind, String name, long value) {
        this.kind = kind;
        this.name = name;
        this.value = value;
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