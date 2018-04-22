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
    private IRNode addNode(Set<PairEdge<IRNode, E>> edges, Set<IRNode> visited, IRSeq body) {
        assert edges.size() == 1;
        PairEdge<IRNode, E> edge = edges.iterator().next();

        if (visited.contains(edge.tail)) {
            throw XicInternalException.internal("Trying to add an IR node twice!");
        }
        visited.add(edge.tail);
        body.add(edge.tail);
        return edge.tail;
    }

    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(name, body);

        Set<IRNode> visited = new HashSet<>();
        Set<IRNode> remaining = vertexSet();

        visited.add(start);
        body.add(start);

        IRNode current = start;
        while (current != null) {
            Set<PairEdge<IRNode, E>> edges = outgoingEdgesOf(current);
            if (current instanceof IRCJump) {
                IRCJump cjump = (IRCJump) current;

                // Follow fall through edge
                PairEdge<IRNode, E> e = getEdge(cjump, cjump.trueLabel());
                edges.remove(e);
                current = addNode(edges, visited, body);
            } else if (current instanceof IRJump) {
                current = null;
            } else if (current instanceof IRLabel) {
                current = addNode(edges, visited, body);
            } else if (current instanceof IRMove) {
                current = addNode(edges, visited, body);
            } else if (current instanceof IRReturn) {
                current = null;
            }
            remaining.removeAll(visited);
            if (current == null && remaining.size() > 0) {
                current = remaining.iterator().next();
            }
        }


        return fn;
    }

}
