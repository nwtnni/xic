package emit;

import type.*;
import util.Context;
import xic.XicInternalException;

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
     */
    public String mangleFunction(String name) {
        GlobalType type = gc.lookup(name);
        if (type instanceof FnType) {
            name = name.replaceAll("_", "__");
            return "_I" + name + "_" + type.toString();
        } else {
            throw XicInternalException.runtime("bad function " + name);
        }
    }

    public String mangleMethod(String name, ClassType t) {
        MethodType type = gc.lookup(t).lookupMethod(name);
        if (type instanceof FnType) {
            name = name.replaceAll("_", "__");
            return "_I" + t.getID() + "_" + name + "_" + type.toString();
        } else {
            throw XicInternalException.runtime("bad method " + name);
        }
    }

    /**
     * Utility method for mangling global variable name to conform to
     * ABI specification.
     */
    public String mangleGlobal(String name) {
        GlobalType type = gc.lookup(name);
        if (type instanceof FieldType) {
            name = name.replaceAll("_", "__");
            return "_I_g_" + name + "_" + type.toString();
        } else {
            throw XicInternalException.runtime("bad global " + name);
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
