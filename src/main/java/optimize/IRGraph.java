package optimize;

import java.util.Set;
import java.util.HashSet;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends PairEdgeGraph<IRNode, E> {
    
    public IRGraph(String name, IRNode start, IREdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.name = name;
    }

    private String name;

    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(name, body);

        Set<IRNode> visited = new HashSet<>();
        Set<IRNode> remaining = vertexSet();

        visited.add(start);
        body.add(start);

        while (remaining.size() > 0) {
            // take a full trace
        }

        return fn;
    }

}
