package util;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.*;

import util.PairEdge;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class PairEdgeGraph<V, E> extends DefaultDirectedGraph<V, PairEdge<V, E>> {
    public V start;
    
    public PairEdgeGraph(V start, EdgeFactory<V, PairEdge<V, E>> edgeFactory) {
        super(edgeFactory);
        this.start = start;
        addVertex(start);
    }

}
