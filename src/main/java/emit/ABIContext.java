package emit;

import java.util.Map;

import type.Context;
import type.FnContext;
import type.FnType;
import type.Type;

public class ABIContext extends Context<String, String> {

    public ABIContext(FnContext context) {
        for (Map.Entry<String,FnType> e : context.getMap().entrySet()) {
            add(e.getKey(), makeABIName(e.getKey(), e.getValue()));
        }
    }

    /**
     * Utility method for mangling function name to conform to
     * ABI specification.
     */
    protected static String makeABIName(String name, FnType type) {
        String args = type.args.toString();
        String returns = type.returns.toString();
        String p = type.returns.equals(Type.UNIT) ? "p" : "";
        name = name.replaceAll("_", "__");
        return "_I" + name + "_" + p + returns + args;
    }
}