package optimize;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import assemble.instructions.*;
import util.PairEdge;
import util.PairEdgeGraph;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class ASAGraph<E> extends PairEdgeGraph<Instr, E> {
    
    public ASAGraph(String name, Instr start, ASAEdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.name = name;
    }

    private String name;

    public FuncDecl toASA() {
        // TODO: figure out dealing with prelude and epilogue
        List<Instr> body = new ArrayList<>();
        FuncDecl fn = new FuncDecl(name, null, body, null);

        Set<Instr> visited = new HashSet<>();
        Set<Instr> remaining = vertexSet();

        visited.add(start);
        body.add(start);

        while (remaining.size() > 0) {
            // take a full trace
        }

        return fn;
    }

}
