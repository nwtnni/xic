package emit;

import java.util.Map;

import type.Context;
import type.FnContext;
import type.FnType;
import type.Type;
import xic.XicInternalException;

public class ABIContext extends Context<String, String> {

    // Reference to the original function to types context
    public FnContext reverseContext;

    public ABIContext(FnContext context) {
        reverseContext = new FnContext();

        for (Map.Entry<String,FnType> e : context.getMap().entrySet()) {
            String mangled = makeABIName(e.getKey(), e.getValue());
            add(e.getKey(), mangled);
            reverseContext.add(mangled, e.getValue());
        }

        reverseContext.add(Emitter.ARRAY_ALLOC, new FnType(Type.INT, Type.INT));
        reverseContext.add(Emitter.ARRAY_CONCAT, new FnType(Type.listFromTypes(Type.INT, Type.INT), Type.INT));
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

    /**
     * Get the number of args given a mangled function name.
     */
    public int getNumArgs(String name) {
        FnType t = reverseContext.lookup(name);
        if (t == null) {
            throw XicInternalException.internal("Non-existent function in ABI.");
        }
        return t.args.size();
    }

    /**
     * Get the number of returns given a mangled function name.
     */
    public int getNumReturns(String name) {
        FnType t = reverseContext.lookup(name);
        if (t == null) {
            throw XicInternalException.internal("Non-existent function in ABI.");
        }
        return t.returns.size();
    }
}