package optimize;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultDirectedGraph;

import ir.*;
import util.PairEdge;

@SuppressWarnings("serial")
public class IRGraph<E> extends DefaultDirectedGraph<IRNode, PairEdge<IRNode, E>> {
    
    public IRGraph(EdgeFactory<IRNode, PairEdge<IRNode, E>> edgeFactory) {
        super(edgeFactory);
    }

    public IRFuncDecl toIR() {
        return null;
    }

}
