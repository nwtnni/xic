package optimize.register;

import java.util.HashSet;
import java.util.Set;

import assemble.Temp;
import assemble.instructions.Instr;
import optimize.graph.ASAEdgeFactory;
import util.PairEdge;

/** Superclass for all IR control flow graph edges. */
public class LVEdgeFactory extends ASAEdgeFactory<Set<Temp>> {
    @Override
    public PairEdge<Instr, Set<Temp>> createEdge(Instr a, Instr b) {
        return new PairEdge<>(a, b, new HashSet<>());
    }
}
