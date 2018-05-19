package type;

/**
 * Represents a top-level type in the OXi type system.
 */
public abstract class GlobalType extends Type {

    @Override
    public boolean isGlobal() { return true; }

}
