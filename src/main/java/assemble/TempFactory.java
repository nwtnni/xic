package assemble;

import interpret.Configuration;

/**
 * Unique temporary generators for IR productions.
 */
public class TempFactory {

    private static long tempIndex = 0;
    
    /**
     * Generate a new temporary name.
     */
    public static String generate() {
        return "asm__temp_" + Long.toString(++tempIndex);
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static String generate(String name) {
        return name + "_asm__temp_" + Long.toString(++tempIndex);
    }
    
    /*
     * Reset the counter for generating temps.
     */
    public static void reset() {
        tempIndex = 0;
    }
    
}
