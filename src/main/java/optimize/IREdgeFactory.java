package optimize;

import org.jgrapht.*;

import ir.*;
import util.PairEdge;

/** Superclass for all IR control flow graph edges. */
abstract class IREdgeFactory<E> implements EdgeFactory<IRNode, PairEdge<IRNode, E>> {
    @Override
    public PairEdge<IRNode, E> createEdge(IRNode a, IRNode b) {
        return new PairEdge<>(a, b, null);
    }
}