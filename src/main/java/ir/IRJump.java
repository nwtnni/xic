package ir;

/**
 * An intermediate representation for a transfer of control
 */
public class IRJump extends IRStmt {
    private IRExpr target;
    private IRLabel targetLabel;

    /**
     *
     * @param expr the destination of the jump
     */

    public IRJump(IRExpr n) {
        target = n;
    }

    public IRJump(IRLabel l) {
        this.target = new IRName(l.name());
        this.targetLabel = l;
    }

    public IRNode target() {
        return target;
    }

    public void setTarget(IRExpr n) {
        target = n;
    }

    public void setTarget(IRLabel l) {
        target = new IRName(l.name());
        targetLabel = l;
    }

    public IRLabel targetLabel() {
        return targetLabel;
    }

    public boolean hasLabel() {
        return targetLabel != null;
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
}
