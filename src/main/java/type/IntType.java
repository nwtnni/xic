package type;

/**
 * Singleton class representing the primitive integer type in the OXi type system.
 */
public class IntType extends GlobalType {

    public static IntType INT = new IntType();

    // Sealed constructor
    private IntType() {}

    @Override
    public boolean isInt() { return true; }

    @Override
    public String toString() { return "i"; }
}
