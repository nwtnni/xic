package assemble;

/**
 * Represents an immediate operand for assembly instructions.
 */
public class AbstractImm {

    private long value;

    public static AbstractImm imm(long value) {
        return new AbstractImm(value);
    }

    private AbstractImm(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
