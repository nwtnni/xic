package emit;

import ir.IRFuncDecl;
import ir.IRReturn;

class Initializer {

    public static final String INIT_FUNCTION = "_init";

    /**
     * TODO: generate initialization function based on program context
     */
    public static IRFuncDecl generateInit() {
        IRFuncDecl fn = new IRFuncDecl(INIT_FUNCTION, "_I_init_p");
        fn.add(new IRReturn());
        
        return fn;
    }
}