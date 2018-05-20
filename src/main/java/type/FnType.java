package type;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a function type in the OXi type system.
 */
public class FnType extends GlobalType {

    protected List<Type> args;
    protected List<Type> rets;

    public FnType(List<Type> args, List<Type> returns) {
        this.args = new ArrayList<>(args);
        this.rets = new ArrayList<>(returns);
    }

    @Override
    public boolean isFn() { return true; }

    public List<Type> getArgs() { return new ArrayList<>(args); }

    public int getNumArgs() { return args.size(); }

    public List<Type> getReturns() { return new ArrayList<>(rets); }

    public int getNumRets() { return rets.size(); }

    @Override
    public String toString() { 
        String a = args.stream()
            .map(e -> e.toString())
            .reduce("", (acc, s) -> acc + s);

        String r = rets.stream()
            .map(e -> e.toString())
            .reduce("", (acc, s) -> acc + s);

        String p = rets.size() == 1 && rets.get(0).equals(UnitType.UNIT) ? "p" : "";

        return p + r + a;
    }

}
