package optimize.register;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import assemble.Temp; 
import assemble.CompUnit; 
import assemble.Reg; 
import assemble.instructions.Instr;

import util.PairEdge;

public class ColorGraph {

    private Graph<Temp, PairEdge<Temp, Void>> graph;
    private Set<Temp> vertices;
    private Map<Temp, Reg> coloring;
    private Set<Reg> available;

    public ColorGraph(List<Instr<Temp>> instructions, Map<Instr<Temp>, Set<Temp>> liveVars, Set<Reg> available) {

        this.graph = new DefaultDirectedGraph<>((a, b) -> new PairEdge<>(a, b, null));
        this.vertices = new HashSet<>();
        this.coloring = new HashMap<>();
        this.available = new HashSet<>(available);
    
        for (Instr<Temp> instr : instructions) {
            List<Temp> live = new ArrayList<>(liveVars.get(instr));
            int size = live.size();

            for (int i = 0; i < size; i++) {
                Temp t = live.get(i);
                graph.addVertex(t);
                vertices.add(t);
                if (t.isFixed()) coloring.put(t, t.getRegister());
            }
            
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    graph.addEdge(live.get(i), live.get(j));
                }
            }
        }
    }

    private boolean isColored(Temp t) {
        return coloring.containsKey(t);
    }

    /**
     * Attempts to color the given temp without conflicting with existing neighbors.
     *
     * Doesn't modify state if unsuccessful.
     */
    public boolean tryColor(Temp t) {

        if (isColored(t)) return true;
        
        // Copy available set
        Set<Reg> available = new HashSet<Reg>(this.available);
        
        // Disqualify all neighboring colors
        for (Temp s: Graphs.neighborListOf(graph, t)) {
            if (isColored(s)) available.remove(coloring.get(s));
        }

        // No available colors left
        if (available.size() == 0) return false;

        // Choose any color
        Reg color = available.stream()
            .findAny()
            .get();

        coloring.put(t, color);
        return true;
    }

    /**
     * Checks if the graph coloring is complete and all nodes are colored.
     */
    public boolean isDone() {
        return vertices.size() == coloring.size();
    }

    /**
     * Retrieves the final mapping from temps to operands.
     *
     * Requires that the coloring is complete.
     */
    public Map<Temp, Reg> getColoring() {
        assert isDone(); 
        return coloring;
    }
}
