package optimize.register;

import java.util.Set;
import java.util.Stack;
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
    private Stack<Temp> stack;

    private Allocator(FuncDecl fn) {
        this.fn = fn; 
        this.stack = new Stack();
    }
    
    // Main try -> spill loop
    private FuncDecl process() {

        InterferenceGraph interfere = new InterferenceGraph(fn.stmts, available.size());
        ColorGraph color = new ColorGraph(fn.stmts, available);
        

        //TODO
        return fn;
    }

    // Returns empty if colorable with spills
    // Otherwise false and must spill the returned Temp
    //
    // Colors the provided ColorGraph
    private Optional<Temp> tryAllocate(InterferenceGraph interfere, ColorGraph color) {

        while (interfere.size() > 0) {
            interfere.pop().ifPresentOrElse(
                temp -> stack.push(temp),
                () -> stack.push(interfere.spill().get())
            );
        }
        
        while (stack.size() > 0) {
            
            Temp temp = stack.pop();

            if (!color.tryColor(temp)) {
                return Optional.of(temp);
            }
        }
        
        return Optional.empty();
    }

    // Mutates FuncDecl fn to spill the temp
    // Must rerun live variable analysis, reconstruct Interference && Color graphs
    private void spill(Temp t) {

    }
}
