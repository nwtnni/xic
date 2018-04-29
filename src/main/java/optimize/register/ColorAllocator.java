package optimize.register;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Optional;

import assemble.*;
import assemble.instructions.*;
import assemble.instructions.BinOp.Kind;

import util.Either;

import xic.XicInternalException;

// TODO: refactor this like the trivial allocator
public class ColorAllocator extends Allocator {

    private static final Set<Reg> available = Set.of(
        Reg.RAX,
        Reg.RBX,
        Reg.RCX,
        Reg.RDX,
        Reg.RSI,
        Reg.RDI,
        Reg.R8,
        Reg.R9,
        Reg.R10,
        Reg.R11,
        Reg.R12,
        Reg.R13,
        Reg.R14,
        Reg.R15
    );

    /**
     * Result of register allocation using graph coloring.
     */
    private Map<Temp, Reg> coloring;

    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        ColorAllocator allocator = new ColorAllocator(unit);
        return allocator.allocate();
    }

    protected Optional<Reg> allocate(Temp t, int index) {
        return (coloring.containsKey(t)) ? Optional.of(coloring.get(t)) : Optional.empty();
    }

    protected ColorAllocator(CompUnit<Temp> unit) {
        super(unit);
        this.coloring = null;
    }

    private CompUnit<Reg> allocate() {
        for (FuncDecl<Temp> fn : unit.fns) {
            allocate(fn);
        }
        return null;
    }

    private void allocate(FuncDecl<Temp> fn) {

        FuncDecl.R allocatedFn = new FuncDecl.R(fn);
        instrs = new ArrayList<>();
        Set<Reg> saved = new HashSet<>();
        maxArgs = 0;
        maxRets = 0;
        coloring = null;

        // TODO loop and spill
        Either<Temp, Map<Temp, Reg>> spilled = tryColor(fn);
        assert spilled.isRight();
        coloring = spilled.getRight();

        for (Reg reg : coloring.values()) {
            if (reg.isCalleeSaved() && !saved.contains(reg)) {
                saved.add(reg);
                allocatedFn.saveRegister(reg);
            }
        }

        for (Instr<Temp> i : fn.stmts) i.accept(this);
        allocatedFn.stmts = instrs;
        allocated.fns.add(allocatedFn);

        // Calculate words to shift rsp, +1 to offset tempCounter
        // TODO: add in result of spilling temps
        int rsp = maxArgs + maxRets;

        // 16 byte alignment
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        Imm shift = new Imm(rsp * Config.WORD_SIZE);

        allocatedFn.setStackSize(rsp);
    }

    // Returns empty if colorable with spills
    // Otherwise false and must spill the returned Temp
    //
    // Colors the provided ColorGraph
    private Either<Temp, Map<Temp, Reg>> tryColor(FuncDecl<Temp> fn) {

//         Stack<Temp> stack = new Stack<>();
//         InterferenceGraph interfere = new InterferenceGraph(fn.stmts, available.size());
//         ColorGraph color = new ColorGraph(fn.stmts, available);

//         while (interfere.size() > 0) {
//             interfere.pop().ifPresentOrElse(
//                 temp -> stack.push(temp),
//                 () -> stack.push(interfere.spill().get())
//             );
//         }

//         while (stack.size() > 0) {
//             Temp temp = stack.pop();
//             if (!color.tryColor(temp)) return Either.left(temp);
//         }

//         return Either.right(color.getColoring());
        return null;
    }
}
