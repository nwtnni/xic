package optimize.register;

import java.util.Set;
import java.util.HashSet;

import assemble.*;
import assemble.instructions.*;
import optimize.*;
import optimize.graph.*;
import util.PairEdge;
import xic.XicInternalException;

public class LiveVariableWorklist extends Worklist<ASAGraph<Set<Temp>>, Instr, Set<Temp>> {

    public LiveVariableWorklist(ASAGraph<Set<Temp>> cfg) {
        super(cfg, Direction.BACKWARDS);
    }

    /** 
     * The transfer function is use[n] union (out[n] - def[n])
     */
    public Set<Temp> transfer(Set<Temp> out, Instr v) {
        Set<Temp> in = v.use;
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
     * (the out set defined by the meet)
     * Returns true if set of live variables has changed.
     */
    public boolean annotate(Instr v, Set<Temp> out) {
        if (v.live.containsAll(out) && out.containsAll(v.live)) {
            return false;
        }
        return true;
    }
}