package ir;

import interpret.Configuration;

public class IRTempFactory {

	private static long tempIndex = 0;
	
    /**
     * Generate a new temporary name.
     */
	public static IRTemp generateTemp() {
        return new IRTemp("__temp_" + Long.toString(++tempIndex));
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static IRTemp generateTemp(String name) {
        return new IRTemp(name + "__temp_" + Long.toString(++tempIndex));
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
	
}