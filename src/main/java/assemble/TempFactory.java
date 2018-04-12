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
        return Temp.temp("asm__temp_" + Long.toString(++tempIndex));
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static Temp generate(String name) {
        return Temp.temp(name + "_asm__temp_" + Long.toString(++tempIndex));
    }
    
    /*
     * Reset the counter for generating temps.
     */
    public static void reset() {
        tempIndex = 0;
    }
    
}
