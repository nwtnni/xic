package optimize;

import java.util.HashMap;
import java.util.Map;

import assemble.*;
import assemble.instructions.*;

public class ASAGraphFactory<E> {

    public ASAGraphFactory(CompUnit compUnit, ASAEdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
    }

    /** The compilation unit to generate CFGs from. */
    private CompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private ASAEdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private ASAGraph<E> cfg;

    /** Previous statement in the assembly. */
    private Instr prev;

    /** Returns the list of CFGs for the compilation unit. */
    public Map<String, ASAGraph<E>> getCfgs() {
        Map<String, ASAGraph<E>> fns = new HashMap<>();
        for (FuncDecl fn : compUnit.fns) {
            // Generate graph
            fns.put(fn.name, cfg);
        }
        return fns;
    }

    public void toCfg(FuncDecl fn) {
        cfg = new ASAGraph<>(fn.sourceName, fn.name, fn.stmts.get(0), edgeFactory);
        // Loop
    }

}