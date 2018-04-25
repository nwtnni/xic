package optimize;


import ir.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Visitor for annotating IRExprs with use, def, gen, kill, exprs
 */
public class CSEReplaceVisitor extends IRVisitor<IRExpr> {

    public IRExpr replaceExpr;
    public IRExpr newExpr;

    public CSEReplaceVisitor() {
        replaceExpr = null;
        newExpr = null;
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

    public IRExpr visit(IRExp e) {
        IRExpr n = e.expr().accept(this);
        if (n != null) {
            e.setExpr(n);
        }
        return null;
    }

    // TODO: check if calls - shouldn't be in lowered IR
    public IRExpr visit(IRCall c) {
        for (IRExpr a : c.args()) {
            IRExpr n = a.accept(this);
            if (n != null) {
                a = newExpr;
            }
        }
        return null;
    }

    public IRExpr visit(IRCJump c) {
        IRExpr n = c.cond.accept(this);
        if (n != null) {
            c.cond = n;
        }
        return null;
    }

    public IRExpr visit(IRJump j) {
        IRExpr n = j.target().accept(this);
        if (n != null) {
            j.setTarget(n);
        }
        return null;
    }

    public IRExpr visit(IRLabel l) {
        return null;
    }

    public IRExpr visit(IRMove m) {
        IRExpr n = m.src.accept(this);
        if (n != null) {
            m.src = n;
        }
        return null;
    }

    public IRExpr visit(IRReturn r) {
        List<IRExpr> newRets = new ArrayList<IRExpr>();
        for (IRExpr n : r.rets()) {
            IRExpr v = n.accept(this);
            if (v != null) {
                newRets.add(v);
            } else {
                newRets.add(n);
            }
        }
        r.setRets(newRets);
        return null;
    }

    public IRExpr visit(IRSeq s) {
        for (IRStmt n : s.stmts()) {
            n.accept(this);
        }
        
        return null;
    }

    /*
     * Expression nodes
     */

    // only binops basically can be eliminated
    public IRExpr visit(IRBinOp b) {
        if (b.equals(replaceExpr)) {
            return newExpr;
        } else {
            IRExpr l = b.left.accept(this);
            if (l != null) {
                b.left = l;
            }
            IRExpr r = b.right.accept(this);
            if (r != null) {
                b.right = r;
            }
        }

        return null;
    }
    
    public IRExpr visit(IRConst c) {
        return null;
    }
    
    public IRExpr visit(IRMem m) {
        IRExpr n = m.expr.accept(this);
        if (n != null) {
            m.expr = n;
        }
        return null;
    }

    public IRExpr visit(IRName n) {
        return null;
    }

    public IRExpr visit(IRTemp t) {
        // should we be adding temps to exprs?
        return null;
    }
    
}
