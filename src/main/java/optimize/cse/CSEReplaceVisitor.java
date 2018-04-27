package optimize.cse;


import ir.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Visitor for finding the IRExpr to replace with the IRTemp generated
 * during common subexpression analysis
 *
 * Visitor propogates up newExpr if it should be replaced by parent node
 */
public class CSEReplaceVisitor extends IRVisitor<IRExpr> {

    public IRExpr replaceExpr;
    public IRExpr newExpr;

    public CSEReplaceVisitor() {
        replaceExpr = null;
        newExpr = null;
    }

    // TODO: add visit(IRExp e) if added to lowering
   
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

    // TODO: update this if we improve lowering of IRCalls
    public IRExpr visit(IRCall c) {
        for (int i = 0; i < c.size(); i++) {
            IRExpr n = c.get(i).accept(this);
            if (n != null) {
                c.set(i, newExpr);
            }
        }
        return null;
    }


    // Basically only binops can be eliminated
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
    
    // TODO: can we replace mems?
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
        return null;
    }
    
}
