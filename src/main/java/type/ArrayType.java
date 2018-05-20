package type;

/**
 * Represents an array type constructor in the OXi type system.
 */
public class ArrayType extends FieldType {

    private Type child;

    public ArrayType(Type child) {
        this.child = child;
    }

    public Type getChild() {
        return child;
    }

    @Override
    public boolean isArray() { return true; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArrayType)) return false;
        ArrayType a = (ArrayType) o;
        return child.equals(a.child);
    }

    @Override
    public int hashCode() {
        return 1 + child.hashCode();
    }

    @Override
    public String toString() {
        return "a" + child.toString();
    }

}
