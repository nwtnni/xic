package optimize.graph;

import java.util.HashMap;
import java.util.Map;

import assemble.instructions.Label;
import emit.IRLabelFactory;
import ir.*;

public class IRGraphFactory<E> extends IRVisitor<IRStmt> {

    public IRGraphFactory(IRCompUnit compUnit, IREdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
    }

    private enum State { IN_BLOCK, OUT_OF_BLOCK; }

    /** The compilation unit to generate CFGs from. */
    private IRCompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private IREdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private IRGraph<E> cfg;

    /** 
     * State is used to determine whether a jump needs to be injected for
     * a fall-through to a label.
     */
    private State state;

    /** Previous statement in the IR. */
    private IRStmt prev;

    /** Returns the list of CFGs for the compilation unit. */
    public Map<String, IRGraph<E>> getCfgs() {
        Map<String, IRGraph<E>> fns = new HashMap<>();
        for (IRFuncDecl fn : compUnit.functions().values()) {
            visit(fn);
            fns.put(fn.name(), cfg);
        }
        return fns;
    }

    public IRStmt visit(IRFuncDecl f) {
        state = State.IN_BLOCK;
        IRLabel start = IRLabelFactory.generate("START");
        prev = start;
        cfg = new IRGraph<>(f.sourceName(), f.name(), prev, edgeFactory);
        f.body().accept(this);

        return null;
    }

    public IRStmt visit(IRSeq s) {
        for (IRStmt n : s.stmts()) {
            prev = n.accept(this);
        }
        return null;
    }

    public IRStmt visit(IRCJump c) {
        switch (state) {
            case IN_BLOCK:
                cfg.addVertex(c);
                cfg.addVertex(c.trueLabel());
                cfg.addEdge(c, c.trueLabel());
                cfg.addEdge(prev, c);

                // CJumps don't end a block because they fall-through
                state = State.IN_BLOCK;
                return c;
            case OUT_OF_BLOCK:
                // Encountered unreachable code
                return null;

            default:
                assert false;
                return null;
        }
    }

    public IRStmt visit(IRJump j) {
        switch (state) {
            case IN_BLOCK:
                cfg.addVertex(j);
                cfg.addVertex(j.targetLabel());
                cfg.addEdge(j, j.targetLabel());
                cfg.addEdge(prev, j);

                // Jumps end a block
                state = State.OUT_OF_BLOCK;
                return null;

            case OUT_OF_BLOCK:
                // Encountered unreachable code
                return null;

            default:
                assert false;
                return null;
        }
    }

    public IRStmt visit(IRLabel l) {
        switch (state) {
            case IN_BLOCK:
                // In case of two labels in a row, inject an explict jump
                // before second label to make reordering safe
                IRJump fallThrough = new IRJump(l);
                cfg.addVertex(fallThrough);
                cfg.addEdge(prev, fallThrough);
                cfg.addVertex(l);
                cfg.addEdge(fallThrough, l);

            case OUT_OF_BLOCK:
                cfg.addVertex(l);

                // Label starts a block
                state = State.IN_BLOCK;
                return l;

            default:
                assert false;
                return null;
        }
    }

    public IRStmt visit(IRMove m) {
        switch (state) {
            case IN_BLOCK:
                cfg.addVertex(m);
                cfg.addEdge(prev, m);

                // Move continues a block
                state = State.IN_BLOCK;
                return m;

            case OUT_OF_BLOCK:
                // Encountered unreachable code
                return null;

            default:
                assert false;
                return null;
        }
    }

    public IRStmt visit(IRReturn r) {
        switch (state) {
            case IN_BLOCK:
                cfg.addVertex(r);
                cfg.addEdge(prev, r);

                // Return ends a block
                state = State.OUT_OF_BLOCK;
                return null;

            case OUT_OF_BLOCK:
                // Encountered unreachable code
                return null;

            default:
                assert false;
                return null;
        }
    }
}
