package optimize;

import java.util.List;
import java.util.ArrayList;

import ir.*;

public class IRGraphFactory<E> extends IRVisitor<IRNode> {

    public IRGraphFactory(IRCompUnit compUnit, IREdgeFactory<E> edgeFactory) {
        this.compUnit = compUnit;
        this.edgeFactory = edgeFactory;
        this.prev = null;
    }

    /** The compilation unit to generate CFGs from. */
    private IRCompUnit compUnit;

    /** The edge factory used to contruct edges in the CFG */
    private IREdgeFactory<E> edgeFactory;

    /** Current CFG being constructed. */
    private IRGraph<E> cfg;

    /** Previous statement in the IR. */
    private IRNode prev;

    /** Returns the list of CFGs for the compilation unit. */
    public List<IRGraph<E>> getCfgs() {
        List<IRGraph<E>> fns = new ArrayList<>();
        for (IRFuncDecl fn : compUnit.functions.values()) {
            visit(fn);
            fns.add(cfg);
        }
        return fns;
    }

    public IRNode visit(IRFuncDecl f) {
        prev = null;

        IRSeq body = (IRSeq) f.body.accept(this);
        cfg = new IRGraph<>(body.stmts.get(0), edgeFactory);

        return null;
    }

    public IRNode visit(IRSeq s) {
        for (IRNode n : s.stmts) {
            cfg.addVertex(n);
            if (prev != null) {
                cfg.addEdge(prev, n);
            }
            n.accept(this);
        }
        return s;
    }

    public IRNode visit(IRCJump c) {
        cfg.addVertex(c.trueLabel);
        cfg.addEdge(c, c.trueLabel);
        prev = c;
        return c;
    }

    public IRNode visit(IRJump j) {
        cfg.addVertex(j.label);
        cfg.addEdge(j, j.label);
        prev = null;
        return j;
    }

    public IRNode visit(IRLabel l) {
        prev = l;
        return l;
    }

    public IRNode visit(IRMove m) {
        prev = m;
        return m;
    }

    public IRNode visit(IRReturn r) {
        prev = null;
        return r;
    }
}