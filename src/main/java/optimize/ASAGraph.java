package optimize;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Deque;
import java.util.Set;

import assemble.*;
import assemble.instructions.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicException;
import xic.XicInternalException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class ASAGraph<E> extends PairEdgeGraph<Instr, E> {
    
    public ASAGraph(String sourceName, String name, Instr start, ASAEdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.sourceName = sourceName;
        this.name = name;
    }

    /** The original function name from source. */
    private String sourceName;

    /** The name of the function associated with this CFG. */
    private String name;

    /**
     * Converts CFG back to abstract assembly code. 
     */
    public FuncDecl toASA() {
        // TODO: figure out dealing with prelude and epilogue
        List<Instr> body = new ArrayList<>();
        FuncDecl fn = new FuncDecl(name, null, body, null);

        Set<Instr> visited = new HashSet<>();
        Deque<Instr> traces = new ArrayDeque<>();
        traces.push(start);

        while (traces.size() > 0) {
            Instr current = traces.poll();

            // Add instruction to assembly
            if (visited.contains(current)) {
                if (current instanceof Label) {
                    continue;
                } else {
                    throw XicInternalException.runtime("Trying to add label twice from ASA CFG!");
                }
            }
            visited.add(current);
            body.add(current);

            // Get next instruction and update traces
            Set<PairEdge<Instr, E>> edges = new HashSet<>(outgoingEdgesOf(current));
            // Do traces.push here
        }

        return fn;
    }

    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, sourceName, phase);
        super.exportCfg(filename);
    }

}
