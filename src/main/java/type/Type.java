package type;

/**
 * Represents a type in the OXi type system.
 */
public abstract class Type {

    //
    // Primitive Types
    //

    public boolean isBool() { return false; }

    public boolean isInt() { return false; }

    public boolean isUnit() { return false; }

    public boolean isVoid() { return false; }

    public boolean isPoly() { return false; }

    // User-defined Types

    public boolean isClass() { return false; }

    public boolean isFn() { return false; }
    
    public boolean isMethod() { return false; }

    //
    // Type Constructors
    //

    public boolean isArray() { return false; }
}
