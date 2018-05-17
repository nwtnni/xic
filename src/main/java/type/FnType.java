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

}
