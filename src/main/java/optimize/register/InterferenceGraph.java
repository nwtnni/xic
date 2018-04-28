package optimize.register;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import util.PairEdge;

import assemble.Temp; 
import assemble.CompUnit; 
import assemble.instructions.Instr;

public class InterferenceGraph {

    private Graph<Temp, PairEdge<Temp, Void>> graph;
    private Set<Temp> vertices;
    private int colors;

    public InterferenceGraph(List<Instr> instructions, int colors) {

        graph = new DefaultDirectedGraph<>((a, b) -> new PairEdge<>(a, b, null));
        vertices = new HashSet<>();
    
        for (Instr instr : instructions) {
        
            List<Temp> live = new ArrayList<>(instr.in);
            int size = live.size();

            for (int i = 0; i < size; i++) {
                graph.addVertex(live.get(i));
                vertices.add(live.get(i));
            }
            
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    graph.addEdge(live.get(i), live.get(j));
                }
            }
        }
    }

    public int size() {
        return vertices.size();
    }

    public Optional<Temp> pop() {
        Optional<Temp> temp = vertices.stream()
            .filter(t -> graph.degreeOf(t) < colors)
            .findAny();

        temp.ifPresent(t -> {
            graph.removeVertex(t);
            vertices.remove(t);
        });

        return temp;
    }

    public Optional<Temp> spill() {
        Optional<Temp> temp = vertices.stream()
            .filter(t -> graph.degreeOf(t) >= colors)
            .findAny();
        
        temp.ifPresent(t -> {
            graph.removeVertex(t);
            vertices.remove(t);
        });

        return temp;
    }
}
