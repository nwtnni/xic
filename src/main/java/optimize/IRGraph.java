package optimize;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends PairEdgeGraph<IRNode, E> {
    
    public IRGraph(IRNode start, IREdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
    }

    public IRFuncDecl toIR() {
        return null;
    }

}
