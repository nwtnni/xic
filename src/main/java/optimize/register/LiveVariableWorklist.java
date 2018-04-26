package optimize.register;

import java.util.Set;
import java.util.HashSet;

import assemble.*;
import assemble.instructions.*;
import optimize.*;
import optimize.graph.*;
import util.PairEdge;

/**
 * Worklist for live variable analysis.
 */
public class LiveVariableWorklist extends Worklist<ASAGraph<Set<Temp>>, Instr, Set<Temp>> {

    /**
     * Initializes the worklist on the graph [cfg].
     * Requires that the graph is initialized to all empty sets on the edges.
     * This ensures that old liveness information stored on the instructions
     * will be overwritten by a fresh liveness analysis.
     */
    public LiveVariableWorklist(ASAGraph<Set<Temp>> cfg) {
        super(cfg, Direction.BACKWARDS);
    }

    /** 
     * The transfer function is use[n] union (out[n] - def[n])
     */
    public Set<Temp> transfer(Set<Temp> m, Instr v) {
        Set<Temp> in = new HashSet<>(v.use);
        Set<Temp> out = new HashSet<>(m);
        out.removeAll(v.def);
        in.addAll(out);
        return in;
    }

    /** 
     * The meet operation is Union(in[n'])
     */
    public Set<Temp> meet(Set<PairEdge<Instr,Set<Temp>>> inPaths) {
        Set<Temp> out = new HashSet<>();
        for (PairEdge<Instr, Set<Temp>> path : inPaths) {
            out.addAll(path.value);
        }
        return out;
    }

    /**
     * Annotates a node [v] with the set of live variables 
     * (the in set defined by the applying the transfer on the meet)
     * Returns true if set of live variables has changed.
     */
    public boolean annotate(Instr v, Set<Temp> in, Set<Temp> out) {
        if (v.in.containsAll(in) && in.containsAll(v.in)) {
            return false;
        }
        v.in = in;
        v.out = out;
        return true;
    }
}