package optimize.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;

import ir.*;
import util.PairEdge;
import util.PairEdgeGraph;
import xic.XicException;
import xic.XicInternalException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class IRGraph<E> extends PairEdgeGraph<IRStmt, E> {
    
    public IRGraph(String sourceName, String name, IRStmt start, IREdgeFactory<E> edgeFactory) {
        super(start, edgeFactory);
        this.sourceName = sourceName;
        this.name = name;
    }

    /** The original function name from source. */
    private String sourceName;

    /** The name of the function associated with this CFG. */
    private String name;

    /** 
     * Gets the successor node of the given node.
     * Requires: there is only a single outgoing edge from this node.
     * */
    private IRStmt getSuccessor(IRStmt node) {
        assert outDegreeOf(node) == 1;
        return outgoingEdgesOf(node).stream().findFirst().get().tail;
    }

    /**
     * Converts CFG back to IR tree.
     */
    public IRFuncDecl toIR() {
        IRSeq body = new IRSeq();
        IRFuncDecl fn = new IRFuncDecl(sourceName, name, body);

        Set<IRStmt> visited = new HashSet<>();
        Deque<IRStmt> traces = new ArrayDeque<>();
        traces.push(start);

        while (traces.size() > 0) {
            IRStmt current = traces.poll();

            // Check and update visited set
            if (visited.contains(current)) {
                // Can visit labels multiple times
                if (current instanceof IRLabel) {
                    continue;
                }
                throw XicInternalException.runtime("Trying to add node twice from IR CFG!");
            }
            visited.add(current);
            // body.add(current);

            // Update traces
            if (current instanceof IRCJump) {
                IRCJump jump = (IRCJump) current;
                body.add(jump);

                // Push branch trace
                traces.push(jump.trueLabel());

                // Push fall-through trace if it is different from the branch
                if (outDegreeOf(jump) > 1) {
                    assert outDegreeOf(jump) == 2;
                    IRStmt next = outgoingEdgesOf(jump).stream()
                        .filter(e -> !e.tail.equals(jump.trueLabel()))
                        .findFirst().get().tail;
                    traces.push(next);
                }
            } else if (current instanceof IRJump) {
                IRJump jump = (IRJump) current;
                body.add(jump);

                if (jump.hasLabel()) {
                    // Start new trace with target
                    traces.push(jump.targetLabel());
                } else {
                    // TODO: Handle arbitrary jumps
                }
            } else if (current instanceof IRLabel) {
                IRLabel label = (IRLabel) current;

                // Remove prior jumps and cjumps if equvialent to a fall-through
                // Keeps on searching only if a fall-through is found
                List<IRStmt> preds = Graphs.predecessorListOf(this, label);
                for (int last = body.size() - 1; last > 0; last--) {
                    IRStmt prev = body.get(last);
                    if (prev instanceof IRJump) {
                        IRJump jmp = (IRJump) prev;
                        if (jmp.targetLabel().equals(label)) {
                            body.remove(last);
                            preds.remove(jmp);
                            last--;
                            continue;
                        }
                    } else if (prev instanceof IRCJump) {
                        IRCJump jmp = (IRCJump) prev;
                        if (jmp.trueLabel().equals(label)) {
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
            } else if (current instanceof IRMove || current instanceof IRExp) {
                body.add(current);
                traces.push(getSuccessor(current));
            } else if (current instanceof IRReturn) {
                body.add(current);

                // Trace ends with return
            }
        }

        return fn;
    }

    /**
     * Writes dot file of CFG.
     */
    public void exportCfg(String basename, String phase) throws XicException {
        String filename = String.format("%s_%s_%s.dot", basename, sourceName, phase);
        super.exportCfg(filename);
    }

}
