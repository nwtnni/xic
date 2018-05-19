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
        String t = String.format("_A%04d", tempIndex++);
        return new Temp(t);
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static Temp generate(String name) {
        String t = String.format("_A%04d_%s", tempIndex++, name);
        return new Temp(t);
    }

    /*
     * Reset the counter for generating temps.
     */
    public static void reset() {
        tempIndex = 0;
    }
    
}
