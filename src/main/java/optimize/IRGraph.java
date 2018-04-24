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
    
    public IRGraph(String name, IRStmt start, IREdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.name = name;
    }

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

    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(name, body);

        Set<IRStmt> visited = new HashSet<>();
        Deque<IRStmt> traces = new ArrayDeque<>();
        traces.push(start);

        while (traces.size() > 0) {
            IRStmt current = traces.poll();

            if (visited.contains(current)) {
                if (current instanceof IRLabel) {
                    continue;
                } else {
                    throw XicInternalException.runtime("Trying to add IR node twice from IR CFG!");
                }
            }
            visited.add(current);
            body.add(current);

            Set<PairEdge<IRStmt, E>> edges = new HashSet<>(outgoingEdgesOf(current));
            if (current instanceof IRCJump) {
                // Remove the edge to the label if it is different from the fall-through
                if (edges.size() > 1) {
                    PairEdge<IRStmt, E> toLabel = getEdge(current, ((IRCJump) current).trueLabel());
                    edges.remove(toLabel);
                    traces.push(toLabel.tail);
                }
                traces.push(getSuccessor(edges));
            } else if (current instanceof IRJump) {
                // Trace ends with jump
                IRJump j = (IRJump) current;
                if (j.hasLabel()) {
                    traces.push(j.targetLabel());
                } else {
                    // Handle arbitrary jumps
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

    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, name, phase);
        super.exportCfg(filename);
    }

}
