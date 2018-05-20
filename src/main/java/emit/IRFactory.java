package emit;

import type.ClassType;
import interpret.Configuration;
import ir.IRTemp;
import ir.IRMem.MemType;
import ir.IRMem;
import ir.IRLabel;

/**
 * Unique temporary generators for IR productions.
 */
public class IRFactory {

    private static long tempIndex = 0;

    private static int labelIndex = 0;

    public static IRLabel generateLabel(String name) {
        String label = String.format("L%04d_%s", labelIndex++, name);
        return new IRLabel(label);
    }
    
    /**
     * Generate a new temporary name.
     */
    public static IRTemp generate() {
        String t = String.format("_T%04d", tempIndex++);
        return new IRTemp(t);
    }

    /**
     * Generate a new temporary with a descriptive name.
     */
    public static IRTemp generate(String name) {
        String t = String.format("_T%04d_%s", tempIndex++, name);
        return new IRTemp(t);
    }

    /**
     * Generates a global.
     */
    public static IRMem generateGlobal(String name, ABIContext context) {
        return new IRMem(new IRTemp(context.mangleGlobal(name)), MemType.GLOBAL);
    }

    /**
     * Generates a class size global.
     */
    public static IRMem generateSize(String name, ABIContext context) {
        return new IRMem(new IRTemp(context.classSize(name)), MemType.GLOBAL);
    }

    /**
     * Generates a class vt global.
     */
    public static IRMem generateVT(String name, ABIContext context) {
        return new IRMem(new IRTemp(context.classVT(name)), MemType.GLOBAL);
    }

    /**
     * Generates a function address.
     */
    public static IRMem generateFuncAddr(String name, ABIContext context) {
        return new IRMem(
            new IRMem(new IRTemp(context.mangleFunction(name)), MemType.GLOBAL),
            MemType.GLOBAL
        );
    }


    /**
     * Generates a method address.
     */
    public static IRMem generateMethodAddr(String name, ClassType type, ABIContext context) {
        return new IRMem(
            new IRMem(new IRTemp(context.mangleMethod(name, type)), MemType.GLOBAL),
            MemType.GLOBAL
        );
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
