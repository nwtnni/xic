package type;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a function type in the OXi type system.
 */
public class FnType extends GlobalType {

    protected List<Type> args;
    protected List<Type> returns;

    public FnType(List<Type> args, List<Type> returns) {
        this.args = new ArrayList<>(args);
        this.returns = new ArrayList<>(returns);
    }

    @Override
    public boolean isFn() { return true; }

    public List<Type> getArgs() { return new ArrayList<>(args); }

    public List<Type> getReturns() { return new ArrayList<>(returns); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FnType)) return false;

        FnType mt = (FnType) o;
        if (mt.args.size() != args.size() || mt.returns.size() != returns.size()) return false;

        for (int i = 0; i < args.size(); i++) {
            if (!mt.args.get(i).equals(args.get(i))) return false;  
        }

        for (int i = 0; i < returns.size(); i++) {
            if (!mt.returns.get(i).equals(returns.get(i))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return args.hashCode() + returns.hashCode();
    }
}
