package emit;

import interpret.Configuration;
import ir.IRTemp;

/**
 * Unique temporary generators for IR productions.
 */
public class IRTempFactory {

    private static long tempIndex = 0;
    
    /**
     * Generate a new temporary name.
     */
    public static IRTemp generate() {
        return new IRTemp("_temp_" + Long.toString(++tempIndex));
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static IRTemp generate(String name) {
        return new IRTemp("_temp_" + Long.toString(++tempIndex) + "_" + name);
    }

    /**
     * Generate the temp for argument i.
     */
    public static IRTemp getArgument(int i) {
        return new IRTemp(Configuration.ABSTRACT_ARG_PREFIX + i);
    }

    /**
     * Generate the temp for return i.
     */
    public static IRTemp getReturn(int i) {
        return new IRTemp(Configuration.ABSTRACT_RET_PREFIX + i);
    }

    /*
     * Reset the counter for generating temps.
     */
    public static void reset() {
        tempIndex = 0;
    }
    
}
