package optimize.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import assemble.*;
import assemble.instructions.*;
import optimize.*;
import optimize.graph.*;
import util.Pair;

/**
 * Worklist for live variable analysis.
 */
public class LiveVariableWorklist extends Worklist<ASAGraph<Set<Temp>>, Instr<Temp>, Set<Temp>> {


    /**
     * Runs the constant propagation analysis on graph [cfg] and returns the mapping of available
     * constants at each program point.
     */
    public static Map<Instr<Temp>, Set<Temp>> computeLiveVariables(ASAGraph<Set<Temp>> cfg) {
        LiveVariableWorklist wl = new LiveVariableWorklist(cfg);
        wl.doWorklist();
        return wl.live;
    }

    /**
     * Initializes the worklist on the graph [cfg].
     * Requires that the graph is initialized to all empty sets on the edges.
     */
    private LiveVariableWorklist(ASAGraph<Set<Temp>> cfg) {
        super(cfg, Direction.BACKWARDS);
        Pair<Map<Instr<Temp>, Set<Temp>>, Map<Instr<Temp>, Set<Temp>>> init = LVInitVisitor.init(cfg.vertexSet());
        this.use = init.first;
        this.def = init.second;
        this.live = new HashMap<>();
    }

    /** Use set at each program point. */
    private Map<Instr<Temp>, Set<Temp>> use;
    
    /** Def set at each program point. */
    private Map<Instr<Temp>, Set<Temp>> def;

    /** Set of live variables (in set) for each program point. */
    private Map<Instr<Temp>, Set<Temp>> live;

    /** 
     * The transfer function is use[n] union (out[n] - def[n])
     */
    public Set<Temp> transfer(Set<Temp> m, Instr<Temp> v) {
        Set<Temp> in = new HashSet<>(use.get(v));
        Set<Temp> out = new HashSet<>(m);
        out.removeAll(def.get(v));
        in.addAll(out);
        return in;
    }

    /** 
     * The meet operation is Union(in[n'])
     */
    public Set<Temp> meet(Set<Set<Temp>> inPaths) {
        Set<Temp> out = new HashSet<>();
        for (Set<Temp> path : inPaths) {
            out.addAll(path);
        }
        return out;
    }

    /**
     * Annotates a node [v] with the set of live variables 
     * (the in set defined by the applying the transfer on the meet)
     * Returns true if set of live variables has changed.
     */
    public boolean annotate(Instr<Temp> v, Set<Temp> in, Set<Temp> out) {
        if (live.containsKey(v)) {
            if (live.get(v).containsAll(in) && in.containsAll(live.get(v))) {
                return false;
            }
        }
        live.put(v, in);
        return true;
    }
}