package optimize.graph;

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
    
    public ASAGraph(FuncDecl fn, Instr start, ASAEdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.sourceName = fn.sourceName;
        this.name = fn.name;
        this.orignalFn = fn;
    }

    /** The original function name from source. */
    private String sourceName;

    /** The name of the function associated with this CFG. */
    private String name;

    private FuncDecl orignalFn;

    /** 
     * Adds the tail node from the edge in edges to a set of 
     * visited nodes and the sequence of nodes. 
     * Requires: edges contains at most 1 edge.
     * */
    private Instr getSuccessor(Set<PairEdge<Instr, E>> edges) {
        assert edges.size() == 1;
        if (edges.iterator().hasNext()) {
            return edges.iterator().next().tail;
        } 
        return null;
    }

    /**
     * Converts CFG back to abstract assembly code. 
     */
    public FuncDecl toASA() {
        // TODO: prelude and epilogue not included in cfg
        List<Instr> body = new ArrayList<>();
        FuncDecl fn = new FuncDecl(orignalFn);
        fn.stmts = body;

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
                    throw XicInternalException.runtime("Trying to add instruction twice from ASA CFG!");
                }
            }
            visited.add(current);
            body.add(current);

            // Get next instruction and update traces
            Set<PairEdge<Instr, E>> edges = new HashSet<>(outgoingEdgesOf(current));
            if (current instanceof Jcc) {
                // Add trace label if it is different from the fall-through
                if (edges.size() > 1) {
                    Jcc jcc = (Jcc) current;
                    PairEdge<Instr, E> toLabel = getEdge(current, jcc.target);
                    edges.remove(toLabel);
                    traces.push(toLabel.tail);
                }
                traces.push(getSuccessor(edges));
            } else if (current instanceof Jmp) {
                // Trace ends with jump
                Jmp jmp = (Jmp) current;
                if (jmp.hasLabel()) {
                    // Start new trace if not jump to return
                    if (!jmp.label.equals(fn.returnLabel)) {
                        traces.push(jmp.label);
                    }
                } else {
                    // TODO: handle arbitary jumps
                }
            } else if (current instanceof Ret) {
                // Trace ends with return
            } else {
                traces.push(getSuccessor(edges));
            }
        }

        return fn;
    }

    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, sourceName, phase);
        super.exportCfg(filename);
    }

}
