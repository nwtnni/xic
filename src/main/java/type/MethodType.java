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
        if (!(o instanceof MethodType)) return false;

        MethodType mt = (MethodType) o;
        if (mt.args.size() != args.size() || mt.returns.size() != returns.size()) return false;

        for (int i = 0; i < args.size(); i++) {
            if (!mt.args.get(i).equals(args.get(i))) return false;  
        }

        for (int i = 0; i < returns.size(); i++) {
            if (!mt.returns.get(i).equals(returns.get(i))) return false;
        }

        return true;
    }

}
