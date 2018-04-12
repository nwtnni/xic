package assemble;

/** Some special stack-related names that are used in the 
 * IR to assembly transation 
 */
public class Config {

    /** Prefix for argument registers */
    public static final String ABSTRACT_ARG_PREFIX = "_ARG";

    /** Prefix for return registers */
    public static final String ABSTRACT_RET_PREFIX = "_RET";

    /** 
     * Name of the callee temp for address to put multiple returns > 2
     */
    public static final Temp CALLEE_MULT_RETURN = Temp.temp("_CALLEE_RET_ADDR");

    /** 
     * Name of the caller temp for address to receive multiple returns > 2
     */
    public static final Temp CALLER_MULT_RETURN = Temp.temp("_CALLER_RET_ADDR");

    /** Word size; assumes a 64-bit architecture */
    public static final int WORD_SIZE = 8;

    /** _xi_alloc function name */
    public static final String XI_ALLOC = "_xi_alloc";
    
    /** _xi_out_of_bounds function name */
    public static final String XI_OUT_OF_BOUNDS = "_xi_out_of_bounds";
}