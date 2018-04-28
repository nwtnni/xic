package optimize;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import util.PairEdge;
import util.PairEdgeGraph;

/** 
 * The generic worklist algorthim abstract class. 
 * Supports both forwards and backwards analyses.
 */
public abstract class Worklist<G extends PairEdgeGraph<V,E>, V, E> {

    public enum Direction { FORWARD, BACKWARDS };

    protected G graph;

    protected Direction direction;

    /** 
     * Construct a worklist for graph g with analysis direction d
     * and top value t.
     */
    protected Worklist(G g, Direction d) {
        this.graph = g;
        this.direction = d;
    }

    /** 
     * Takes the result of the meet [m] and applys the transfer function
     * at node [v].
     */
    public abstract E transfer(E m, V v);

    /** 
     * Meet function takes a set of the paths [paths] and merges 
     * them with the meet operator.
     */
    public abstract E meet(Set<PairEdge<V,E>> paths);

    /**
     * Updates a node [v] by taking meet of all paths and applying the
     * transfer function in the direction [direction].
     */
    public boolean update(V v) {
        if (direction == Direction.FORWARD) {
            // Forward analysis
            E in = meet(graph.incomingEdgesOf(v));
            E out = transfer(in, v);

            if (annotate(v, in, out)) {
                for (PairEdge<V,E> outEdge : graph.outgoingEdgesOf(v)) {
                    update(outEdge, out);
                }
                return true;
            }
        } else {
            // Backwards analysis
            E out = meet(graph.outgoingEdgesOf(v));
            E in = transfer(out, v);
            if (annotate(v, in, out)) {
                for (PairEdge<V,E> inEdge : graph.incomingEdgesOf(v)) {
                    update(inEdge, in);
                }
                return true;
            }
        }
        // No change has been made at this node
        return false;
    }

    /**
     * Annotates a node [v] with the in and out sets calculated by 
     * the meet and transfer.
     * Returns true if value has changed, otherwise false.
     */
    public abstract boolean annotate(V v, E in, E out);

    /**
     * Updates an edge with [value].
     */
    public void update(PairEdge<V,E> edge, E value) {
        edge.value = value;
    }

    /**
     * Get the children of node [v] in relation to [direction].
     */
    protected List<V> getChildren(V v) {
        List<V> children = new ArrayList<>();
        if (direction == Direction.FORWARD) {
            // Forward analysis
            for (PairEdge<V,E> outEdge : graph.outgoingEdgesOf(v)) {
                children.add(outEdge.tail);
            }
        } else {
            for (PairEdge<V,E> inEdge : graph.incomingEdgesOf(v)) {
                children.add(inEdge.head);
            }
        }
        return children;
    }

    /**
     * Run the worklist algorithm on [graph] until convergence.
     */
    public void doWorklist() {
        Deque<V> worklist = new LinkedList<>(graph.vertexSet());

        while (!worklist.isEmpty()) {
            V node = worklist.poll();
            boolean updated = update(node);
            if (updated) {
                for (V child : getChildren(node)) {
                    worklist.push(child);
                }
            }
        }
    }
}
