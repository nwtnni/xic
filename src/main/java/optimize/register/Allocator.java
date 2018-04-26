package optimize.register;

import java.util.Set;
import java.util.Optional;

import assemble.Temp;
import assemble.Operand;
import assemble.FuncDecl;
import assemble.CompUnit;
import assemble.instructions.Instr;

public class Allocator {

    private static final Set<Operand> available = Set.of(
        Operand.R10,
        Operand.R11
    );

    public static CompUnit allocate(CompUnit unit) {

        return unit;
    }

    private FuncDecl fn;
    private Stack<Temp> popped;

    private Allocator(FuncDecl fn) {
        this.fn = fn; 
    }
    
    // Main try -> spill loop
    private FuncDecl process() {

    }

    // Returns empty if colorable with spills
    // Otherwise false and must spill the returned Temp
    private Optional<Temp> tryAllocate() {


    }

    // Mutates FuncDecl fn to spill the temp
    // Must rerun live variable analysis, reconstruct Interference && Color graphs
    private void spill(Temp t) {

    }
    
    
}
