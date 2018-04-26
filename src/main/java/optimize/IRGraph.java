package optimize;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Deque;
import java.util.Set;


import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicException;
import xic.XicInternalException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends PairEdgeGraph<IRStmt, E> {
    
    public IRGraph(String sourceName, String name, IRStmt start, IREdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.sourceName = sourceName;
        this.name = name;
    }

    /** The original function name from source. */
    private String sourceName;

    /** The name of the function associated with this CFG. */
    private String name;

    /** 
     * Adds the tail node from the edge in edges to a set of 
     * visited nodes and the sequence of nodes. 
     * Requires: edges contains a single edge.
     * */
    private IRStmt getSuccessor(Set<PairEdge<IRStmt, E>> edges) {
        assert edges.size() == 1;
        return edges.iterator().next().tail;
    }

    /**
     * Converts CFG back to IR tree.
     */
    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(sourceName, name, body);

        Set<IRStmt> visited = new HashSet<>();
        Deque<IRStmt> traces = new ArrayDeque<>();
        traces.push(start);

        while (traces.size() > 0) {
            IRStmt current = traces.poll();

            // Add node to IR
            if (visited.contains(current)) {
                if (current instanceof IRLabel) {
                    continue;
                } else {
                    throw XicInternalException.runtime("Trying to add node twice from IR CFG!");
                }
            }
            visited.add(current);
            body.add(current);

            // Get next node and update traces
            Set<PairEdge<IRStmt, E>> edges = new HashSet<>(outgoingEdgesOf(current));
            if (current instanceof IRCJump) {
                // Add trace to the label if it is different from the fall-through
                if (edges.size() > 1) {
                    IRCJump jump = (IRCJump) current;
                    PairEdge<IRStmt, E> toLabel = getEdge(current, jump.trueLabel());
                    edges.remove(toLabel);
                    traces.push(toLabel.tail);
                }
                traces.push(getSuccessor(edges));
            } else if (current instanceof IRJump) {
                // Trace ends with jump
                IRJump j = (IRJump) current;
                if (j.hasLabel()) {
                    // Start new trace with target
                    traces.push(j.targetLabel());
                } else {
                    // TODO: Handle arbitrary jumps
                }
            } else if (current instanceof IRLabel) {
                traces.push(getSuccessor(edges));
            } else if (current instanceof IRMove) {
                traces.push(getSuccessor(edges));
            } else if (current instanceof IRReturn) {
                // Trace ends with return
            }
        }

        return fn;
    }

    /**
     * Writes dot file of CFG.
     */
    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, sourceName, phase);
        super.exportCfg(filename);
    }

}
