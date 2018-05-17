package type;

/**
 * Singleton class representing a primitive boolean type in the OXi type system.
 */
public class BoolType extends GlobalType {

    public static final BoolType BOOL = new BoolType();

    // Sealed constructor
    private BoolType() {}

    @Override
    public boolean isBool() { return true; }

    @Override
    public String toString() { return "b"; }
}
