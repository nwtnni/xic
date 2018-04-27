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
    
    public Void visit(IRCompUnit c) {
        for (IRFuncDecl fd : c.functions().values()) {
            fd.accept(this);
        }
        return null;
    }

    public Void visit(IRFuncDecl f) {
        f.body().accept(this);
        return null;
    }

    /*
     * Statement nodes
     */


    // TODO: check if calls - shouldn't be in lowered IR
    public Void visit(IRCall c) {

        for (IRNode a : c.args()) {
            a.accept(this);
            c.exprs.addAll(a.exprs);
        }
        c.delMem = true;
        return null;
    }

    public Void visit(IRCJump c) {
        c.cond.accept(this);
        c.exprs = c.cond.exprs;
        c.delMem = c.cond.delMem;
        return null;
    }

    public Void visit(IRJump j) {
        j.target().accept(this);
        j.exprs = j.target().exprs;
        j.delMem = j.target().delMem;
        return null;
    }

    public Void visit(IRLabel l) {
        return null;
    }

    public Void visit(IRMove m) {
        m.src.accept(this);
        m.exprs.addAll(m.src.exprs);
        m.kill.add(m.target());

        if (m.target.delMem || m.src.delMem) {
            m.delMem = true;
        }
        return null;
    }

    public Void visit(IRReturn r) {
        for (IRExpr n : r.rets()) {
            n.accept(this);
            r.exprs.addAll(n.exprs);
            if (n.delMem) {
                r.delMem = true;
            }
        }
        return null;
    }

    public Void visit(IRSeq s) {
        for (IRStmt n : s.stmts()) {
            n.accept(this);
            s.exprs.addAll(n.exprs);
            if (n.delMem) {
                s.delMem = true;
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
        if (b.left.delMem || b.right.delMem) {
            b.delMem = true;
        }

        return null;
    }
    
    public Void visit(IRConst c) {
        return null;
    }
    
    public Void visit(IRMem m) {
        m.expr.accept(this);
        m.exprs.addAll(m.expr.exprs);
        m.exprs.add(m);
        m.delMem = true;

        return null;
    }

    public Void visit(IRName n) {
        return null;
    }

    public Void visit(IRTemp t) {
        // should we be adding temps to exprs?
        t.exprs.add(t);
        return null;
    }
    
}
