package type;

/**
 * Represents the null type in the OXi type system.
 */
public class NullType extends FieldType {

    public static NullType NULL = new NullType();

    private NullType() {}

    @Override
    public boolean isNull() { return true; }

}
