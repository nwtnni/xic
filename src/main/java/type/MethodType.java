package type;

import java.util.List;

/**
 * Represents a method type with an implicit argument in the OXi type system.
 */
public class MethodType extends FnType {

    private ClassType implicit;

    public MethodType(ClassType implicit, List<Type> args, List<Type> returns) {
        super(args, returns);
        this.implicit = implicit;
    }

    @Override
    public boolean isMethod() { return true; }

    public ClassType getImplicit() { return implicit; }

}
