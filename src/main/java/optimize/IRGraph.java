package optimize;

import org.jgrapht.graph.DefaultDirectedGraph;

import ir.*;
import util.PairEdge;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends DefaultDirectedGraph<IRNode, PairEdge<IRNode, E>> {
    public IRNode start;
    
    public IRGraph(IRNode start, IREdgeFactory<E> edgeFactory) {
        super(edgeFactory);
        this.start = start;
        addVertex(start);
    }

    public IRFuncDecl toIR() {
        return null;
    }

}
