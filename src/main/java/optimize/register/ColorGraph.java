package optimize.register;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import assemble.Temp;
import assemble.Reg;
import assemble.instructions.Instr;
import assemble.instructions.Mov;

import util.PairEdge;
import util.Either;
import util.Pair;

public class ColorGraph {

    private Set<Reg> available;
    private Set<Temp> initial;

    // Interference graph
    private Graph<Temp, PairEdge<Temp, Void>> interfere;
    private Map<Temp, Integer> degree;

    // Temp related sets
    private Set<Temp> simplifyWorklist;
    private Set<Temp> freezeWorklist;
    private Set<Temp> spillWorklist;
    private Set<Temp> spilledNodes;
    private Set<Temp> coloredNodes;
    private Set<Temp> coalescedNodes;
    private Stack<Temp> selectStack;

    // A mapping (t1 -> t2) means t1 is an alias for t2
    private Map<Temp, Temp> alias;

    // Coloring
    private Map<Temp, Reg> color;
    private int colors;

    // Move related sets
    private Map<Temp, Set<Pair<Temp, Temp>>> moveList;
    private Set<Pair<Temp, Temp>> coalescedMoves;
    private Set<Pair<Temp, Temp>> constrainedMoves;
    private Set<Pair<Temp, Temp>> frozenMoves;
    private Set<Pair<Temp, Temp>> worklistMoves;
    private Set<Pair<Temp, Temp>> activeMoves;

    public ColorGraph(List<Instr<Temp>> instructions, Map<Instr<Temp>, Set<Temp>> liveVars, Set<Reg> available) {

        this.available = new HashSet<>(available);
        this.alias = new HashMap<>();
        this.colors = available.size();
        this.color = new HashMap<>();
        this.initial = new HashSet<>();
        this.interfere = new DefaultDirectedGraph<>((a, b) -> new PairEdge<>(a, b, null));
        this.degree = new HashMap<>();

        // Node related sets
        this.simplifyWorklist = new HashSet<>();
        this.freezeWorklist = new HashSet<>();
        this.spillWorklist = new HashSet<>();
        this.spilledNodes = new HashSet<>();
        this.coloredNodes = new HashSet<>();
        this.coalescedNodes = new HashSet<>();
        this.selectStack = new Stack<>();

        // Move related sets
        this.moveList = new HashMap<>();
        this.coalescedMoves = new HashSet<>();
        this.constrainedMoves = new HashSet<>();
        this.frozenMoves = new HashSet<>();
        this.worklistMoves = new HashSet<>();
        this.activeMoves = new HashSet<>();

        for (Instr<Temp> instr : instructions) {

            List<Temp> live = new ArrayList<>(liveVars.get(instr));
            int size = live.size();

            for (int i = 0; i < size; i++) {

                Temp t = live.get(i);
                interfere.addVertex(t);

                if (!t.isFixed()) {
                    initial.add(t);
                } else {
                    color.put(t, t.getRegister());
                }
            }

            if (instr instanceof Mov.TRR) {
                Mov.TRR mov = (Mov.TRR) instr;
                Pair<Temp, Temp> move = new Pair<>(mov.src, mov.dest);

                // Make sure associated sets are not null
                if (!moveList.containsKey(mov.src)) moveList.put(mov.src, new HashSet<>());
                if (!moveList.containsKey(mov.dest)) moveList.put(mov.dest, new HashSet<>());

                moveList.get(mov.src).add(move);
                moveList.get(mov.dest).add(move);
                worklistMoves.add(move);
            }

            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    interfere.addEdge(live.get(i), live.get(j));
                }
            }
        }

        // Initialize degree set
        for (Temp t : interfere.vertexSet()) {
            if (t.isFixed()) {
                degree.put(t, Integer.MAX_VALUE);
            } else {
                degree.put(t, interfere.degreeOf(t));
            }
        }

        // Make worklist
        for (Temp t : initial) {
            if (interfere.degreeOf(t) >= colors) {
                spillWorklist.add(t);
            } else if (moveRelated(t)) {
                freezeWorklist.add(t);
            } else {
                simplifyWorklist.add(t);
            }
        }
    }


    /** Main register allocation function */
    public Either<
        Map<Temp, Reg>, // Complete coloring assignment of Temp to Register
        Set<Temp>       // Set of spilled nodes
    > tryColor() {

        while (!this.simplifyWorklist.isEmpty()
            || !this.worklistMoves.isEmpty()
            || !this.freezeWorklist.isEmpty()
            || !this.spillWorklist.isEmpty()) {

            if (!this.simplifyWorklist.isEmpty()) {
                // System.out.println("Simplifying");
                this.simplify();
            }
            else if (!this.worklistMoves.isEmpty()) {
                // System.out.println("Coalescing");
                this.coalesce();
            }
            else if (!this.freezeWorklist.isEmpty()) {
                // System.out.println("Freezing");
                this.freeze();
            }
            else if (!this.spillWorklist.isEmpty()) {
                // System.out.println("Spilling");
                this.selectSpill();
            }
        }

        if (this.spilledNodes.isEmpty()) {
            return Either.left(this.color);
        } else {
            return Either.right(this.spilledNodes);
        }
    }

    /**
     * Returns coalesced node mappings.
     *
     * All occurrences of Temp [n] should be replaced by Temp getAlias[n].
     */
    public Temp getAlias(Temp n) {
        if (coalescedNodes.contains(n)) {
            return getAlias(alias.get(n));
        } else {
            return n;
        }
    }

    private void addEdge(Temp u, Temp v) {
        if (!(interfere.containsEdge(u, v) || interfere.containsEdge(v, u)) && !u.equals(v)) {
            interfere.addEdge(u, v);
            if (!u.isFixed()) degree.put(u, degree.get(u) + 1);
            if (!v.isFixed()) degree.put(v, degree.get(v) + 1);
        }
    }

    private boolean moveRelated(Temp t) {
        return nodeMoves(t).size() != 0;
    }

    public void simplify() {

        Temp n = simplifyWorklist.stream()
            .findAny()
            .get();

        simplifyWorklist.remove(n);
        selectStack.push(n);

        for (Temp m : adjacent(n)) {
            decrementDegree(m);
        }
    }

    private Set<Temp> adjacent(Temp n) {
        if (!interfere.containsVertex(n)) return new HashSet<>();
        return difference(Graphs.neighborListOf(interfere, n), union(selectStack, coalescedNodes));
    }

    private Set<Pair<Temp, Temp>> nodeMoves(Temp t) {
        if (!moveList.containsKey(t)) moveList.put(t, new HashSet<>());
        return intersect(moveList.get(t), (union(activeMoves, worklistMoves)));
    }

    private void decrementDegree(Temp m) {

        Integer d = degree.get(m);
        degree.put(m, d - 1);

        if (d == colors) {
            Set<Temp> enable = new HashSet<>(adjacent(m));
            enable.add(m);
            enableMoves(enable);
            spillWorklist.remove(m);

            if (moveRelated(m)) {
                freezeWorklist.add(m);
            } else {
                simplifyWorklist.add(m);
            }
        }
    }

    private void enableMoves(Set<Temp> nodes) {
        for (Temp n : nodes) {
            for (Pair<Temp, Temp> m : nodeMoves(n)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    worklistMoves.add(m);
                }
            }
        }
    }

    private void addWorkList(Temp u) {
        if (!u.isFixed() && !moveRelated(u) && degree.get(u) < colors) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    private boolean ok(Temp t, Temp r) {
        return degree.get(t) < colors
            || t.isFixed()
            || interfere.containsEdge(t, r)
            || interfere.containsEdge(r, t);
    }

    private boolean conservative(Set<Temp> nodes) {
        int k = 0;
        for (Temp n : nodes) {
            if (degree.get(n) >= colors) k += 1;
        }
        return k < colors;
    }

    public void coalesce() {
        Pair<Temp, Temp> m = worklistMoves
            .stream()
            .findAny()
            .get();

        worklistMoves.remove(m);

        Temp x = getAlias(m.first);
        Temp y = getAlias(m.second);

        Temp u = (y.isFixed()) ? y : x;
        Temp v = (y.isFixed()) ? x : y;

        if (u.equals(v)) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (v.isFixed() || interfere.containsEdge(u, v) || interfere.containsEdge(v, u)) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if ((u.isFixed() && adjacent(v).stream().allMatch(t -> ok(t, u)))
                || (!u.isFixed() && conservative(union(adjacent(u), adjacent(v))))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else {
            activeMoves.add(m);
        }
    }

    private void combine(Temp u, Temp v) {
        if (!interfere.containsVertex(u) || !interfere.containsVertex(v)) return;

        if (freezeWorklist.contains(v)) {
            freezeWorklist.remove(v);
        } else {
            spillWorklist.remove(v);
        }

        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.put(u, union(moveList.get(u), moveList.get(v)));
        enableMoves(Set.of(v));

        for (Temp t : adjacent(v)) {
            if (interfere.containsVertex(t)) {
                addEdge(t, u);
                decrementDegree(t);
            }
        }

        if (degree.get(u) >= colors && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }

    private void freeze() {
        Temp u = freezeWorklist
            .stream()
            .findAny()
            .get();

        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(Temp u) {
        for (Pair<Temp, Temp> m : nodeMoves(u)) {
            Temp v = null;
            if (getAlias(m.second).equals(getAlias(u))) {
                v = getAlias(m.first);
            } else {
                v = getAlias(m.second);
            }
            activeMoves.remove(m);
            frozenMoves.add(m);
            if (freezeWorklist.contains(v) && nodeMoves(v).size() == 0) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }

    private void selectSpill() {
        // TODO: replace with heuristic?
        Temp m = spillWorklist
            .stream()
            .findAny()
            .get();

        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        freezeMoves(m);
    }

    private void assignColors() {
        while (!selectStack.empty()) {
            Temp n = selectStack.pop();

            Set<Reg> okColors = new HashSet<>(available);
            for (Temp w : Graphs.neighborListOf(interfere, n)) {

                Temp alias = getAlias(w);

                if (alias.isFixed() || coloredNodes.contains(alias)) {
                    okColors.remove(color.get(alias));
                }
            }

            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                coloredNodes.add(n);

                Reg reg = okColors
                    .stream()
                    .findAny()
                    .get();

                color.put(n, reg);
            }
        }
        for (Temp n : coalescedNodes) {
            color.put(n, color.get(getAlias(n)));
        }
    }

    private <T> Set<T> difference(Collection<T> s1, Collection<T> s2) {
        Set<T> difference = new HashSet<>(s1);
        difference.removeAll(s2);
        return difference;
    }

    private <T> Set<T> intersect(Collection<T> s1, Collection<T> s2) {
        Set<T> intersect = new HashSet<>(s1);
        intersect.retainAll(s2);
        return intersect;
    }

    private <T> Set<T> union(Collection<T> s1, Collection<T> s2) {
        Set<T> union = new HashSet<>(s1);
        union.addAll(s2);
        return union;
    }
}
