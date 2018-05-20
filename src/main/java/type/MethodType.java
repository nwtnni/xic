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

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && (o instanceof MethodType);
    }

    @Override
    public int hashCode() {
        return implicit.hashCode() * args.hashCode() * rets.hashCode();
    }

}
