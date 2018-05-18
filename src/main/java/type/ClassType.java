package type;

/**
 * Represents a user-defined class in the OXi type system.
 */
public class ClassType extends FieldType {

    private String id;
    
    public ClassType(String id) {
        this.id = id;
    }

    public String getID() {
        return new String(id);
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClassType)) return false;
        ClassType c = (ClassType) o;
        return c.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
