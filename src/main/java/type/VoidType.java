package type;

import xic.XicInternalException;

/**
 * Singleton class representing the primitive void type in the OXi type system.
 */
public class VoidType extends Type {

    public static final VoidType VOID = new VoidType(); 

    // Sealed constructor.
    private VoidType() {}

    @Override
    public boolean isVoid() { return true; }

    @Override
    public String toString() {
        throw new XicInternalException("Attempting to encode void type");
    }

}
