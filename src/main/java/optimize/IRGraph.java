package optimize;

import java.util.Set;
import java.util.HashSet;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicInternalException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends PairEdgeGraph<IRNode, E> {
    
    public IRGraph(String name, IRNode start, IREdgeFactory<E> edgeFactory) {
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
    private IRNode getSuccessor(Set<PairEdge<IRNode, E>> edges) {
        assert edges.size() == 1;
        return edges.iterator().next().tail;
    }

    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(name, body);

        Set<IRNode> visited = new HashSet<>();
        Set<IRNode> remaining = vertexSet();

        IRNode current = start;
        while (current != null) {
            if (visited.contains(current)) {
                throw XicInternalException.runtime("Trying to add IR node twice from CFG!");
            }
            visited.add(current);
            body.add(current);

            Set<PairEdge<IRNode, E>> edges = outgoingEdgesOf(current);
            if (current instanceof IRCJump) {
                // Follow the fall-through edge
                edges.remove(getEdge(current, ((IRCJump) current).trueLabel()));
                current = getSuccessor(edges);
            } else if (current instanceof IRJump) {
                // Trace ends with jump
                current = null;
            } else if (current instanceof IRLabel) {
                current = getSuccessor(edges);
            } else if (current instanceof IRMove) {
                current = getSuccessor(edges);
            } else if (current instanceof IRReturn) {
                // Trace ends with return
                current = null;
            }

            // Start a new trace if nodes remain
            remaining.removeAll(visited);
            if (current == null && remaining.size() > 0) {
                current = remaining.iterator().next();
            }
        }

        return fn;
    }

}
