package emit;

import type.*;
import util.Context;

public class ABIContext extends Context<String, String> {

    public ABIContext(GlobalContext context) {
        this.gc = context;
    }

    /**
     * Global context this ABIContext is based on.
     */
    public GlobalContext gc;

    /**
     * Utility method for mangling function name to conform to
     * ABI specification.
     * 
     * Returns null if function is not in the context.
     */
    public String mangleFunction(String name) {
        GlobalType type = gc.lookup(name);
        if (type instanceof FnType) {
            name = name.replaceAll("_", "__");
            return "_I" + name + "_" + type.toString();
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
        GlobalType type = gc.lookup(name);
        if (type instanceof FieldType) {
            name = name.replaceAll("_", "__");
            return "_I_g_" + name + "_" + type.toString();
        } else {
            return null;
        }
    }

    /** Returns ABI name for class size global. */
    public String classSize(String name) {
        return "_I_size_" + name.replaceAll("_", "__");
    }

    /** Returns ABI name for class VT global. */
    public String classVT(String name) {
        return "_I_vt_" + name.replaceAll("_", "__");
    }

    /** Returns ABI name for class initialization function global. */
    public String classInit(String name) {
        return "_I_init_" + name.replaceAll("_", "__");
    }

}
