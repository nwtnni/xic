package optimize.graph;

import assemble.instructions.Instr;
import assemble.Temp;
import util.PairEdge;
import util.PairEdgeFactory;

/** Superclass for all IR control flow graph edges. */
public class ASAEdgeFactory<E> implements PairEdgeFactory<Instr<Temp>, E> {
    @Override
    public PairEdge<Instr<Temp>, E> createEdge(Instr<Temp> a, Instr<Temp> b) {
        return new PairEdge<>(a, b, null);
    }
}
