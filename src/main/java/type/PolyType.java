package type;

import xic.XicInternalException;

/**
 * Singleton class representing the polymorphic, length-zero array type.
 */
public class PolyType extends Type {

    public static PolyType POLY = new PolyType();

    // Sealed constructor.
    private PolyType() {}

    @Override
    public boolean isPoly() { return true; }

    @Override
    public String toString() {
        //TODO: is this possible?
        throw new XicInternalException("Attempting to encode polymorphic type");
    }
}
