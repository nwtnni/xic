package optimize;

import assemble.instructions.Instr;
import util.PairEdge;
import util.PairEdgeFactory;

/** Superclass for all IR control flow graph edges. */
public class ASAEdgeFactory<E> implements PairEdgeFactory<Instr, E> {
    @Override
    public PairEdge<Instr, E> createEdge(Instr a, Instr b) {
        return new PairEdge<>(a, b, null);
    }
}
