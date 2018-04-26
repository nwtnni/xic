package optimize.register;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import assemble.Temp; 
import assemble.CompUnit; 
import assemble.Operand; 
import assemble.instructions.Instr;

import util.PairEdge;

public class ColorGraph {

    private Graph<Temp, PairEdge<Temp, Void>> graph;
    private Set<Temp> vertices;
    private Map<Temp, Operand> coloring;
    private Set<Operand> available;

    public ColorGraph(List<Instr> instructions, Set<Operand> available) {

        graph = new DefaultDirectedGraph<>((a, b) -> new PairEdge<>(a, b, null));
        vertices = new HashSet<>();
        coloring = new HashMap<>();
        available = new HashSet<>(available);
    
        for (Instr instr : instructions) {
        
            List<Temp> live = new ArrayList<>(instr.in);
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

        // Copy available set
        Set<Operand> available = new HashSet<Operand>(this.available);
        
        // Disqualify all neighboring colors
        for (PairEdge<Temp, Void> edge : graph.edgesOf(t)) {
            
            Temp s = (edge.head.equals(t)) ? edge.tail : edge.head;

            if (isColored(s)) {
                available.remove(coloring.get(s));
            }
        }

        // No available colors left
        if (available.size() == 0) return false;

        // Choose any color
        Operand color = available.stream()
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
    public Map<Temp, Operand> getColoring() {
        assert isDone(); 
        return coloring;
    }
}
