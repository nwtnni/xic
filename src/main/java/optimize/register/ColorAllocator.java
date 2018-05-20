package optimize.register;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import java.util.Optional;

import assemble.*;
import assemble.instructions.*;

import optimize.graph.*;

import util.*;

/**
 * Allocates registers through graph coloring as per the Appel pseudocode.
 */
public class ColorAllocator extends Allocator {

    /** Allocates registers for all the functions in the compliation unit. */
    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        ColorAllocator allocator = new ColorAllocator(unit);
        return allocator.allocate();
    }

    /** The initial set of registers available for coloring. */
    private static Set<Reg> availableRegs = new HashSet<>(Set.of(
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
    ));

    /**
     * Result of register allocation using graph coloring.
     */
    private Map<Temp, Reg> coloring;

    /** Initialize the allocator with a unit and graph facotory. */
    private ColorAllocator(CompUnit<Temp> unit) {
        super(unit);
        this.coloring = null;
        this.graphFactory = new ASAGraphFactory<>(new LVEdgeFactory());
    }

    private ASAGraphFactory<Set<Temp>> graphFactory;

    private Map<Instr<Temp>, Set<Temp>> use;

    private Map<Instr<Temp>, Set<Temp>> def;

    /**
     * Allocates a temp based on the coloring.
     * index is ignored.
     */
    @Override
    protected Optional<Reg> allocate(Temp t, int index) {
        return (coloring.containsKey(t)) ? Optional.of(coloring.get(t)) : Optional.empty();
    }

    private CompUnit<Reg> allocate() {
        for (FuncDecl<Temp> fn : unit.text) {
            allocate(fn);
        }
        return allocated;
    }

    private void allocate(FuncDecl<Temp> fn) {

        FuncDecl.R allocatedFn = new FuncDecl.R(fn);
        instrs = new ArrayList<>();
        maxArgs = 0;
        maxRets = 0;
        coloring = null;
        int spillOffset = -8;

        while (coloring == null) {
            ASAGraph<Set<Temp>> cfg = graphFactory.makeCfg(fn);
            fn = cfg.toASA();

            // Compute use and def sets
            Pair<Map<Instr<Temp>, Set<Temp>>, Map<Instr<Temp>, Set<Temp>>> init = LVInitVisitor.init(cfg);
            use = init.first;
            def = init.second;

            // Compute live variables set
            Map<Instr<Temp>, Set<Temp>> liveVars = LiveVariableWorklist.computeLiveVariables(cfg, use, def);

            // Remove callee args that have been move coalesced
            availableRegs = GetCalleeRegs.getCalleeRegs(fn);
            availableRegs.addAll(
                Set.of( Reg.RAX,
                        Reg.RCX,
                        Reg.RDX,
                        Reg.RSI,
                        Reg.RDI,
                        Reg.R8,
                        Reg.R9,
                        Reg.R10,
                        Reg.R11)
                );
            
            // Optimistically color and coalesce
            ColorGraph cg = new ColorGraph(fn.stmts, liveVars, availableRegs);
            Either<Map<Temp, Reg>, Set<Temp>> result = cg.tryColor();
            TempReplacer replacer = new TempReplacer(cg); 

            // If successfully colored, move on to allocating
            if (result.isLeft()) {
                coloring = result.getLeft();
                fn.stmts = replacer.replaceAll(fn.stmts);
            // Spill
            } else {
                Set<Temp> spilled = result.getRight();
                fn.stmts = replacer.replaceAll(fn.stmts);
                Spiller spiller = new Spiller(spilled, spillOffset);
                fn.stmts = spiller.spillAll(fn.stmts);
                spillOffset = spillOffset - Config.WORD_SIZE * (spilled.size());
            }
        }

        for (Instr<Temp> i : fn.stmts) {
            i.accept(this);
        }
        allocatedFn.stmts = instrs;
        allocated.text.add(allocatedFn);

        // Calculate number of words to shift %rsp
        int rsp = maxArgs + maxRets + spillOffset / (-8);

        // 16 byte alignment if needed
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        allocatedFn.setStackSize(rsp);
    }

}
