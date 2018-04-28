package ir;

/**
 * An intermediate representation for evaluating an expression for side effects,
 * discarding the result
 * EXP(e)
 */
public class IRExp extends IRStmt {
    public IRExpr expr;

    /**
     *
     * @param expr the expression to be evaluated and result discarded
     */
    public IRExp(IRExpr expr) {
        this.expr = expr;
    }

    public IRNode expr() {
        return expr;
    }

    public void setExpr(IRExpr n) {
        expr = n;
    }

    @Override
    public String label() {
        return "EXP";
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
