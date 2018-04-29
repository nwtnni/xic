package assemble;

/**
 * Represents an immediate operand for assembly instructions.
 */
public class Imm {

    private long value;

    public Imm(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Imm)) return false;
        Imm i = (Imm) o;
        return i.value == value;
    }

    @Override
    public int hashCode() {
        return (int) value;
    }

    @Override
    public String toString() {
        return "$" + Long.toString(value); 
    }
}
