package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for a transfer of control
 */
public class IRJump extends IRStmt {
    private IRNode target;

    /**
     *
     * @param expr the destination of the jump
     */
    public IRJump(IRNode expr) {
        target = expr;
    }

    public IRNode target() {
        return target;
    }

    @Override
    public String label() {
        return "JUMP";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRExpr expr = (IRExpr) v.visit(this, target);

    //     if (expr != target) return v.nodeFactory().IRJump(expr);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(target));
    //     return result;
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("JUMP");
        target.printSExp(p);
        p.endList();
    }
}
