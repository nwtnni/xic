package ir;

import java.util.List;
import java.util.ArrayList;

/**
 * Visitor for generating deep copy of node
 * Erases all metadata
 */
public class IRDeepCopier extends IRVisitor<IRExpr> {


    public IRExpr visit(IRCall c) {
        List<IRExpr> newArgs = new ArrayList<>();
        for (IRExpr e : c.args()) {
            newArgs.add(e.accept(this));
        }
        return new IRCall(c.target().accept(this), c.numRets(), newArgs);
    }

    public IRExpr visit(IRBinOp b) {
        return new IRBinOp(b.type(), b.left().accept(this), b.right().accept(this));
    }
    
    public IRExpr visit(IRConst c) {
        return new IRConst(c.value());
    }
    
    public IRExpr visit(IRMem m) {
        return new IRMem(m.expr().accept(this), m.memType());

    }

    public IRExpr visit(IRName n) {
        return new IRName(n.name());
    }

    public IRExpr visit(IRTemp t) {
        return new IRTemp(t.name());
    }
    
}
