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
     * Takes the result of the meet [m] and applys the transfer function
     * at node [v].
     */
    public Set<Temp> transfer(Set<Temp> m, Instr v) {
        return null;
    }

    /** 
     * Meet function takes a set of the paths [paths] and merges 
     * them with the meet operator.
     */
    public Set<Temp> meet(Set<PairEdge<Instr,Set<Temp>>> paths) {
        return null;
    }

    /**
     * Annotates a node [v] with the result of the meet [e].
     */
    public boolean annotate(Instr v, Set<Temp> e) {
        return false;
    }
}