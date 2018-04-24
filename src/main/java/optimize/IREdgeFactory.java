package optimize;

import ir.IRStmt;
import util.PairEdge;
import util.PairEdgeFactory;

/** Superclass for all IR control flow graph edges. */
public class IREdgeFactory<E> implements PairEdgeFactory<IRStmt, E> {
    @Override
    public PairEdge<IRStmt, E> createEdge(IRStmt a, IRStmt b) {
        return new PairEdge<>(a, b, null);
    }
}
