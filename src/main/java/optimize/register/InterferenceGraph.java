package optimize.register;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import util.PairEdge;
import util.Pair;

import assemble.Temp;
import assemble.CompUnit;
import assemble.instructions.Instr;

public class InterferenceGraph {

    private Graph<Temp, PairEdge<Temp, Void>> graph;
    private Set<Temp> vertices;
    private int colors;

    public InterferenceGraph(List<Instr<Temp>> instructions, Map<Instr<Temp>, Set<Temp>> liveVars, int colors) {

        graph = new DefaultDirectedGraph<>((a, b) -> new PairEdge<>(a, b, null));
        vertices = new HashSet<>();

        for (Instr<Temp> instr : instructions) {

            // TODO: update when LV returns a map
            List<Temp> live = new ArrayList<>(liveVars.get(instr));
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

    // Returns Optional.empty if coalescing fails
    // Otherwise returns [T1, T2] s.t. T1 is now an alias for T2.
    // Replace all instances of T1 with T2.
    public Optional<Pair<Temp, Temp>> coalesce() {

        // Iterate through vertex set
        List<Temp> temps = new ArrayList<>(vertices);

        for (Temp u : temps) {

            if (u.isFixed()) continue;

            // Get set of not interfering neighbors
            Set<Temp> safe = new HashSet<>(vertices);
            safe.removeIf(v -> graph.containsEdge(u, v) || graph.containsEdge(v, u));

            if (safe.size() == 0) continue;
            Set<Temp> uset = new HashSet<>(Graphs.neighborListOf(graph, u));

            // potential candidates
            List<Temp> potCans = new ArrayList<>(safe);

            for (Temp v : potCans) {

                Set<Temp> vset = new HashSet<>(Graphs.neighborListOf(graph, v));
                Set<Temp> union = new HashSet<Temp>(uset);
                vset.removeAll(uset);
                union.addAll(vset);

                // If vertex neighbors union other neighbors < colors, coalesce
                if (union.size() < colors) {

                    vertices.remove(v);
                    graph.removeVertex(v);

                    for (Temp w : vset) {
                        graph.addEdge(u, w);
                    }

                    return Optional.of(new Pair<>(u, v));
                }
            }
        }

        return Optional.empty();
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
