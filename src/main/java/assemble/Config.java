package assemble;

/** 
 * Some special stack-related names that are used in the 
 * IR to assembly transation 
 */
public class Config {

    /** Word size; assumes a 64-bit architecture */
    public static final int WORD_SIZE = 8;

    /** _xi_alloc function name */
    public static final String XI_ALLOC = "_xi_alloc";
    
    /** _xi_out_of_bounds function name */
    public static final String XI_OUT_OF_BOUNDS = "_xi_out_of_bounds";

    /** Get the register for args 0-5 on System V. */
    public static Operand getArg(int i) {
        switch (i) {
            case 0:
                return Operand.RDI;
            case 1:
                return Operand.RSI;
            case 2:
                return Operand.RDX;
            case 3:
                return Operand.RCX;
            case 4:
                return Operand.R8;
            case 5:
                return Operand.R9;
        }
        assert false;
        return null;
    }

    /**
     * Check if value is representable with n-bits in 2's complement notation
     * Loses MIN_INT for each n bits for simplicity
     */
    public static boolean within(int bits, long value) {
        return Math.abs(value) < Math.pow(2, bits - 1) - 1;
    }
}