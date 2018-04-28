package optimize.propagate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ir.*;
import optimize.graph.IREdgeFactory;
import util.PairEdge;

/** 
 * Generates edges for live variable analysis. 
 * Ensures each edge is initialized to an empty set.
 * */
public class ConstEdgeFactory extends IREdgeFactory<Map<IRTemp, Optional<IRConst>>> {
    @Override
    public PairEdge<IRStmt,Map<IRTemp, Optional<IRConst>>> createEdge(IRStmt a, IRStmt b) {
        return new PairEdge<>(a, b, new HashMap<>());
    }
}
