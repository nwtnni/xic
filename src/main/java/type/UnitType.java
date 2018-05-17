package type;

/**
 * Singleton class representing the primitive unit type in the OXi type system.
 */
public class UnitType extends Type {

    public static final UnitType UNIT = new UnitType();

    // Sealed constructor.
    private UnitType() {}

    @Override
    public boolean isUnit() { return true; }

    @Override
    public String toString() { return ""; }
}
