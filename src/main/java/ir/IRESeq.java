package ir;

import java.util.List;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr {
    public IRStmt stmt;
    public IRExpr expr;

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRStmt stmt, IRExpr expr) {
        this.stmt = stmt;
        this.expr = expr;
    }

    /**
     *
     * @param stmt IR statement that allocates memory for an array
     * @param expr IR expression that is address of this array
     * @param values list of values in this array
     */
    public IRESeq(IRStmt stmt, IRExpr expr, List<IRNode> values) {
        this.stmt = stmt;
        this.expr = expr;
        this.values = values;
    }

    /* Public inteface for IRESeq */

    public IRStmt stmt() {
        return stmt;
    }

    public IRExpr expr() {
        return expr;
    }

    @Override
    public String label() {
        return "ESEQ";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRStmt stmt = (IRStmt) v.visit(this, this.stmt);
    //     IRExpr expr = (IRExpr) v.visit(this, this.expr);

    //     if (expr != this.expr || stmt != this.stmt)
    //         return v.nodeFactory().IRESeq(stmt, expr);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(stmt));
    //     result = v.bind(result, v.visit(expr));
    //     return result;
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return false;
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
