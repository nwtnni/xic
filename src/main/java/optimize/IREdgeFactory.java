package optimize;

import ir.IRNode;
import util.PairEdge;
import util.PairEdgeFactory;

/** Superclass for all IR control flow graph edges. */
abstract class IREdgeFactory<E> implements PairEdgeFactory<IRNode, E> {
    @Override
    public PairEdge<IRNode, E> createEdge(IRNode a, IRNode b) {
        return new PairEdge<>(a, b, null);
    }
}
