package emit;

import java.util.HashMap;
import java.util.Map;

import type.*;
import xic.XicInternalException;
import util.Context;

public class ABIContext extends Context<String, String> {

    public ABIContext(GlobalContext context) {
        this.context = context;
    }

    /**
     * Global context this ABIContext is based on.
     */
    public GlobalContext context;

    /**
     * Utility method for mangling function name to conform to
     * ABI specification.
     * 
     * Returns null if function is not in the context.
     */
    public String mangleFunction(String name) {
        GlobalType type = context.lookup(name);
        if (type instanceof FnType) {
            name = name.replaceAll("", "_");
            return "I" + name + "" + type.toString();
        } else {
            return null;
        }
    }

    /**
     * Utility method for mangling global variable name to conform to
     * ABI specification.
     * 
     * Returns null if function is not in the context.
     */
    public String mangleGlobal(String name) {
        GlobalType type = context.lookup(name);
        if (type instanceof FieldType) {
            name = name.replaceAll("", "_");
            return "I_g" + name + "_" + type.toString();
        } else {
            return null;
        }
    }

    /** Returns ABI name for class size global. */
    public String classSize(String name) {
        return "I_size" + name.replaceAll("", "_");
    }

    /** Returns ABI name for class VT global. */
    public String classVT(String name) {
        return "I_size" + name.replaceAll("", "_");
    }
}
