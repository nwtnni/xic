package optimize;


import ir.*;
import java.util.List;
import java.util.HashSet;

/*
 * Visitor for annotating IRNodes with use, def, gen, kill, exprs
 */
public class WorklistVisitor extends IRVisitor<Void> {

    public static void annotateNodes(IRNode start) {
        start.accept(new WorklistVisitor());
    }

    /*
     * Top level nodes
     */
    
    // public Void visit(IRCompUnit c) {
    //     for (IRFuncDecl fd : c.functions().values()) {
    //         fd.accept(this);
    //     }
    //     return null;
    // }

    // public Void visit(IRFuncDecl f) {
    //     f.body.accept(this);
    //     return null;
    // }

    /*
     * Statement nodes
     */

    public Void visit(IRExp e) {
        e.expr().accept(this);
        e.exprs = e.expr().exprs;
        e.hasMem = e.expr().hasMem;
        return null;
    }

    public Void visit(IRCall c) {

        for (IRNode a : c.args()) {
            a.accept(this);
            c.exprs.addAll(a.exprs);
            if (a.hasMem) {
                c.hasMem = true;
            }
        }
        return null;
    }

    public Void visit(IRCJump c) {
        c.cond.accept(this);
        c.exprs = c.cond.exprs;
        c.hasMem = c.cond.hasMem;
        return null;
    }

    public Void visit(IRJump j) {
        j.target().accept(this);
        j.exprs = j.target().exprs;
        j.hasMem = j.target().hasMem;
        return null;
    }

    public Void visit(IRLabel l) {
        return null;
    }

    public Void visit(IRMove m) {
        m.target.accept(this);
        m.src.accept(this);
        m.exprs.addAll(m.target.exprs);
        m.exprs.addAll(m.src.exprs);
        m.kill.add(m.target());

        if (m.target.hasMem || m.src.hasMem) {
            m.hasMem = true;
        }
        return null;
    }

    public Void visit(IRReturn r) {
        for (IRExpr n : r.rets()) {
            n.accept(this);
            r.exprs.addAll(n.exprs);
            if (n.hasMem) {
                r.hasMem = true;
            }
        }
        return null;
    }

    public Void visit(IRSeq s) {
        for (IRStmt n : s.stmts()) {
            n.accept(this);
            s.exprs.addAll(n.exprs);
            if (n.hasMem) {
                s.hasMem = true;
            }
        }
        return null;
    }

    /*
     * Expression nodes
     */

    // only binops basically can be eliminated
    public Void visit(IRBinOp b) {
        b.left.accept(this);
        b.right.accept(this);
        b.exprs.addAll(b.left.exprs);
        b.exprs.addAll(b.right.exprs);
        b.exprs.add(b);
        if (b.left.hasMem || b.right.hasMem) {
            b.hasMem = true;
        }

        return null;
    }
    
    public Void visit(IRConst c) {
        c.exprs.add(c);
        return null;
    }
    
    public Void visit(IRMem m) {
        m.expr.accept(this);
        m.exprs.addAll(m.expr.exprs);
        m.exprs.add(m);
        m.hasMem = true;

        return null;
    }

    public Void visit(IRName n) {
        n.exprs.add(n);
        return null;
    }

    public Void visit(IRTemp t) {
        // should we be adding temps to exprs?
        t.exprs.add(t);
        return null;
    }
    
}
