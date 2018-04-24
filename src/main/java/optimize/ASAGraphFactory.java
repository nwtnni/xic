package optimize;

import java.util.List;
import java.util.ArrayList;

import assemble.*;
import assemble.instructions.*;

public class ASAGraphFactory<E> {

    public ASAGraphFactory(CompUnit compUnit, ASAEdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
        this.prev = null;
    }

    /** The compilation unit to generate CFGs from. */
    private CompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private ASAEdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private ASAGraph<E> cfg;

    /** Previous statement in the IR. */
    private Instr prev;

}