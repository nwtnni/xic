package type;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a function type in the OXi type system.
 */
public class FnType extends GlobalType {

    protected List<Type> args;
    protected List<Type> rets;

    public FnType(List<Type> args, List<Type> rets) {
        this.args = new ArrayList<>(args);
        this.rets = rets.isEmpty() ? List.of(UnitType.UNIT) : new ArrayList<>(rets);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FnType)) return false;

        FnType mt = (FnType) o;
        if (mt.args.size() != args.size() || mt.rets.size() != rets.size()) return false;

        for (int i = 0; i < args.size(); i++) {
            if (!mt.args.get(i).equals(args.get(i))) return false;  
        }

        for (int i = 0; i < rets.size(); i++) {
            if (!mt.rets.get(i).equals(rets.get(i))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return args.hashCode() + rets.hashCode();
    }
}
