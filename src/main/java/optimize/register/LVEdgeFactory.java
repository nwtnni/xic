package optimize.register;

import java.util.HashSet;
import java.util.Set;

import assemble.Temp;
import assemble.instructions.Instr;
import optimize.graph.ASAEdgeFactory;
import util.PairEdge;

/** 
 * Generates edges for live variable analysis. 
 * Ensures each edge is initialized to an empty set.
 * */
public class LVEdgeFactory extends ASAEdgeFactory<Set<Temp>> {
    @Override
    public PairEdge<Instr<Temp>, Set<Temp>> createEdge(Instr<Temp> a, Instr<Temp> b) {
        return new PairEdge<>(a, b, new HashSet<>());
    }
}
