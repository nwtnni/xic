package optimize.propagate;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import ir.*;
import optimize.*;
import optimize.graph.*;
import util.Pair;
import util.PairEdge;

/**
 * Worklist for avaliable constants.
 * Generates a mapping of instructions to the mappings of variables at that position.
 */
public class ConstWorklist extends Worklist<IRGraph<Map<IRTemp, Optional<IRConst>>>, IRStmt, Map<IRTemp, Optional<IRConst>>> {

    /**
     * Runs the constant propagation analysis on graph [cfg] and returns the mapping of available
     * constants at each program point.
     */
    public static Map<IRStmt, Map<IRTemp, Optional<IRConst>>> computeAvailableConsts(IRGraph<Map<IRTemp, Optional<IRConst>>> cfg) {
        ConstWorklist wl = new ConstWorklist(cfg);
        wl.doWorklist();
        return wl.availableConsts;
    }

    /**
     * Initializes the worklist on the graph [cfg].
     * Requires that the graph is initialized to all empty sets on the edges.
     */
    private ConstWorklist(IRGraph<Map<IRTemp, Optional<IRConst>>> cfg) {
        super(cfg, Direction.FORWARD);
        this.gen = ConstInitVisitor.getConstDefs(cfg.vertexSet());
        this.availableConsts = new HashMap<>();
    }

    /** 
     * The set of generated constants at each node. 
     * gen is defined as:
     *      x = c : x -> c
     *      x = e : x -> NAC
     *      All other nodes do not gen.
     */
    private Map<IRStmt, Pair<IRTemp, Optional<IRConst>>> gen;

    /** 
     * The mapping of available variables at each node.
     * For each node:
     *      x -> Opt.Empty() => x -> NAC
     *      x -> Opt.of(c)   => x -> c
     *      x not in mapping => x -> UNDEF
     */
    private Map<IRStmt, Map<IRTemp, Optional<IRConst>>> availableConsts;

    /** 
     * The transfer function is adds or replaces the binding of x if x is defined at [v].
     */
    public Map<IRTemp, Optional<IRConst>> transfer(Map<IRTemp, Optional<IRConst>> in, IRStmt v) {
        Map<IRTemp, Optional<IRConst>> out = new HashMap<>(in);
        
        // Add/replace mapping if this node generates binding for x
        if (gen.containsKey(v)) {
            Pair<IRTemp, Optional<IRConst>> def = gen.get(v);
            out.put(def.first, def.second);
        }

        return out;
    }

    /** 
     * The meet operation is intersection of mappings.
     * Follows:
     *      Remove all conflicting mappings: x -> a ^ x -> b where a != b => x -> NAC
     *      NAC > c > UNDEF
     */
    public Map<IRTemp, Optional<IRConst>> meet(Set<Map<IRTemp, Optional<IRConst>>> paths) {
        // Get set of all variables
        Set<IRTemp> temps = new HashSet<>();
        for (Map<IRTemp, ?> path : paths) {
            temps.addAll(path.keySet());
        }

        // Calculate in set by computing meet by checking each variable against every path
        Map<IRTemp, Optional<IRConst>> in = new HashMap<>();
        for (IRTemp temp : temps) {
            Optional<IRConst> value = null;
            for (Map<IRTemp, Optional<IRConst>> path : paths) {
                // If path contains: temp -> c | NAC
                if (path.containsKey(temp)) {
                    Optional<IRConst> c = path.get(temp);
                    // If temp is still uninitialized: temp -> c
                    if (c.isPresent() && value == null) {
                        value = c;
                    
                    // If there are conflicting bindings: temp -> NAC
                    } else if (c.isPresent() && value.isPresent() && !value.get().equals(c.get())) {
                        value = Optional.empty();
                        break;

                    // If any path maps to NAC: temp -> NAC
                    } else {
                        value = Optional.empty();
                        break;
                    }
                }
            }
            in.put(temp, value);
        }

        return in;
    }

    /**
     * Annotates a node [v] with the map of variables to values.
     * Returns true if set of avaliable constants has changed.
     */
    public boolean annotate(IRStmt v, Map<IRTemp, Optional<IRConst>> in, Map<IRTemp, Optional<IRConst>> out) {
        boolean hasChanged = false;
        
        // Check if bindings have changed from previous in set
        Map<IRTemp, Optional<IRConst>> prev = availableConsts.get(v);
        if (prev != null) {
            // prev <= in so only need to check one side
            if (prev.keySet().containsAll(in.keySet())) {
                for (IRTemp t : prev.keySet()) {
                    // If any binding has changed
                    if (!prev.get(t).equals(in.get(t))) {
                        hasChanged = true;
                    }
                }
            } else {
                hasChanged = true;
            }
        }
            
        // Update mapping (can just overwrite even if no change)
        availableConsts.put(v, in);
        return hasChanged;
    }
}