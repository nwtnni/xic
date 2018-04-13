package assemble;

/**
 * Unique temporary generators for IR productions.
 */
public class TempFactory {

    private static long tempIndex = 0;
    
    /**
     * Generate a new temporary name.
     */
    public static Temp generate() {
        return Temp.temp("_ASM" + Long.toString(++tempIndex));
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static Temp generate(String name) {
        return Temp.temp("_ASM" + Long.toString(++tempIndex) + "_" + name);
    }

    // /**
    //  * Generate the temp for argument i.
    //  */
    // public static Temp getArgument(int i) {
    //     return Temp.temp(Config.ABSTRACT_ARG_PREFIX + i);
    // }

    // /**
    //  * Generate the temp for return i for caller.
    //  */
    // public static Temp getReturn(int i) {
    //     return Temp.temp(Config.CALLER_RET_PREFIX + i);
    // }

   
    // /**
    //  * Generate the temp for return i for callee.
    //  */
    // public static Temp getCalleeReturn(int i) {
    //     return Temp.temp(Config.CALLEE_RET_PREFIX + i);
    // } 

    /*
     * Reset the counter for generating temps.
     */
    public static void reset() {
        tempIndex = 0;
    }
    
}
