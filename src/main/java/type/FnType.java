package type;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a function type in the OXi type system.
 */
public class FnType extends Type {

    private List<Type> args;
    private List<Type> returns;

    public FnType(List<Type> args, List<Type> returns) {
        this.args = args;
        this.returns = returns;
    }

    public List<Type> getArgs() {
        return new ArrayList<>(args);
    }

    public List<Type> getReturns() {
        return new ArrayList<>(returns);
    }

}
