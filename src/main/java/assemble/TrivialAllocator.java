package assemble;

import java.util.List;
import java.util.ArrayList;

import assemble.*;
import assemble.instructions.*;
import emit.ABIContext;

public class TrivialAllocator {

    // Mangled names context
    private ABIContext context;

    // Running list of assembly instructions
    private CompUnit unit;

    // Current function visited
    String funcName;

    // Current list of instructions
    List<Instr> instrs;

    private TrivialAllocator(ABIContext context, CompUnit unit) {
        this.context = context;
        this.unit = unit;
    }

    /**
     * Returns number of return values for a function.
     * Takes the mangled function name.
     */
    private int numReturns(String fn) {
        if (fn.equals(Config.XI_ALLOC)) {
            return 1;
        } else if (fn.equals(Config.XI_OUT_OF_BOUNDS)) {
            return 0;
        }
        return context.getNumReturns(fn);
    }
}