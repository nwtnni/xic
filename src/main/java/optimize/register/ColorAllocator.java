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

import optimize.graph.*;

import util.*;

import xic.XicInternalException;

public class ColorAllocator extends Allocator {

    /** Allocates registers for all the functions in the compliation unit. */
    public static CompUnit<Reg> allocate(CompUnit<Temp> unit) {
        ColorAllocator allocator = new ColorAllocator(unit);
        return allocator.allocate();
    }

    private static final Set<Reg> availableRegs = Set.of(
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
        while (coloring == null) {
            ASAGraph<Set<Temp>> cfg = graphFactory.makeCfg(fn);

            // Compute use and def sets
            Pair<Map<Instr<Temp>, Set<Temp>>, Map<Instr<Temp>, Set<Temp>>> init = LVInitVisitor.init(cfg.vertexSet());
            use = init.first;
            def = init.second;

            // Compute live variables set
            Map<Instr<Temp>, Set<Temp>> liveVars = LiveVariableWorklist.computeLiveVariables(cfg, use, def);

            // Optimistically color and coalesce
            ColorGraph cg = new ColorGraph(fn.stmts, liveVars, availableRegs);
            Either<Map<Temp, Reg>, Set<Temp>> result = cg.tryColor();

            // If successfully colored, move on to allocating
            if (result.isLeft()) {
                coloring = result.getLeft();

            // Spill and 
            } else {
                Set<Temp> spilled = result.getRight();
                

            }

            // Coalesce temps
            TempReplacer.replaceAll(fn, cg);

        }
        
        for (Reg reg : coloring.values()) {
            if (reg.isCalleeSaved() && !saved.contains(reg)) {
                saved.add(reg);
                allocatedFn.saveRegister(reg);
            }
        }

        for (Instr<Temp> i : fn.stmts) i.accept(this);
        allocatedFn.stmts = instrs;
        allocated.fns.add(allocatedFn);


        // TODO: push callee saved

        // Calculate number of words to shift %rsp
        // TODO: add in result of spilling temps
        int rsp = maxArgs + maxRets;

        // 16 byte alignment if needed
        // TODO: account for pushed callee registers (misaligned if # of callee regs pushed is odd)
        rsp = rsp % 2 == 1 ? rsp + 1 : rsp;
        allocatedFn.setStackSize(rsp);
    }

}
