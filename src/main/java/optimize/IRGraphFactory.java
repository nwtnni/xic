package optimize;

import java.util.HashMap;
import java.util.Map;

import ir.*;

public class IRGraphFactory<E> extends IRVisitor<IRStmt> {

    public IRGraphFactory(IRCompUnit compUnit, IREdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
    }

    /** The compilation unit to generate CFGs from. */
    private IRCompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private IREdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private IRGraph<E> cfg;

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
        prev = null;
        cfg = new IRGraph<>(f.sourceName(), f.name(), f.get(0), edgeFactory);
        f.body().accept(this);

        return null;
    }

    public IRStmt visit(IRSeq s) {
        for (IRStmt n : s.stmts()) {
            cfg.addVertex(n);
           
            if (prev != null) {
                cfg.addEdge(prev, n);
            }

            prev = n.accept(this);
        }
        return s;
    }

    public IRStmt visit(IRCJump c) {
        cfg.addVertex(c.trueLabel());
        cfg.addEdge(c, c.trueLabel());
        return c;
    }

    public IRStmt visit(IRJump j) {
        cfg.addVertex(j.targetLabel());
        cfg.addEdge(j, j.targetLabel());
        return null;
    }

    public IRStmt visit(IRLabel l) {
        return l;
    }

    public IRStmt visit(IRMove m) {
        return m;
    }

    public IRStmt visit(IRReturn r) {
        return null;
    }
}