package optimize.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Deque;
import java.util.Set;

import org.jgrapht.Graphs;

import assemble.*;
import assemble.instructions.*;
import util.PairEdgeGraph;
import xic.XicException;
import xic.XicInternalException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class ASAGraph<E> extends PairEdgeGraph<Instr, E> {
    
    public ASAGraph(FuncDecl fn, Instr start, ASAEdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.sourceName = fn.sourceName;
        this.name = fn.name;
        this.orignalFn = fn;
    }

    /** The original function name from source. */
    private String sourceName;

    /** The name of the function associated with this CFG. */
    private String name;

    protected FuncDecl orignalFn;

    /** 
     * Gets the successor node of the given node.
     * Requires: there is a single outgoing edge from this node.
     * */
    private Instr getSuccessor(Instr node) {
        assert outDegreeOf(node) == 1;
        return outgoingEdgesOf(node).stream().findFirst().get().tail;
    }

    /**
     * Converts CFG back to abstract assembly code. 
     */
    public FuncDecl toASA() {
        // TODO: prelude and epilogue not included in cfg
        List<Instr> body = new ArrayList<>();
        FuncDecl fn = new FuncDecl(orignalFn);
        fn.stmts = body;

        Set<Instr> visited = new HashSet<>();
        Deque<Instr> traces = new ArrayDeque<>();
        traces.push(start);

        while (traces.size() > 0) {
            Instr current = traces.poll();

            // Add instruction to assembly
            if (visited.contains(current)) {
                // Can visit labels multiple times
                if (current instanceof Label) {
                    continue;
                } else {
                    throw XicInternalException.runtime("Trying to add instruction twice from ASA CFG!");
                }
            }
            visited.add(current);

            // Get next instruction and update traces
            if (current instanceof Jcc) {
                Jcc jcc = (Jcc) current;
                body.add(jcc);

                traces.push(jcc.target);

                // Push fall-through trace if different from branch
                if (outDegreeOf(jcc) > 1) {
                    assert outDegreeOf(jcc) == 2;
                    Instr next = outgoingEdgesOf(jcc).stream()
                        .filter(e -> !e.tail.equals(jcc.target))
                        .findFirst().get().tail;
                    traces.push(next);
                }
            } else if (current instanceof Jmp) {
                Jmp jmp = (Jmp) current;
                body.add(jmp);

                // Start new trace if not jump to return
                if (jmp.hasLabel() && !jmp.label.equals(fn.returnLabel)) {
                    traces.push(jmp.label);
                } else {
                    // TODO: handle arbitary jumps
                }
            } else if (current instanceof Label) {
                Label label = (Label) current;
                
                // Remove prior jumps and cjumps if equvialent to a fall-through
                // Keeps on searching only if a fall-through is found
                List<Instr> preds = Graphs.predecessorListOf(this, label);
                for (int last = body.size() - 1; last > 0; last--) {
                    Instr prev = body.get(last);
                    if (prev instanceof Jmp) {
                        Jmp jmp = (Jmp) prev;
                        if (jmp.label.equals(label)) {
                            body.remove(last);
                            preds.remove(jmp);
                            last--;
                            continue;
                        }
                    } else if (prev instanceof Jcc) {
                        Jcc jmp = (Jcc) prev;
                        if (jmp.target.equals(label)) {
                            body.remove(last);
                            preds.remove(jmp);
                            last--;
                            continue;
                        }
                    }
                    break;
                }

                // Only add label if exists paths to it
                if (preds.size() > 0) {
                    body.add(current);
                }

                // Follow the fall-through trace
                traces.push(getSuccessor(label));
            } else {
                body.add(current);
                traces.push(getSuccessor(current));
            }
        }

        return fn;
    }

    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, sourceName, phase);
        super.exportCfg(filename);
    }

}
