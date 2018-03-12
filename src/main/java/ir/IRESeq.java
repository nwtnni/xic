package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for an expression evaluated under side effects
 * ESEQ(stmt, expr)
 */
public class IRESeq extends IRExpr {
    private IRNode stmt;
    private IRNode expr;

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    public IRESeq(IRNode stmt, IRNode expr) {
        this.stmt = stmt;
        this.expr = expr;
    }

    public IRNode stmt() {
        return stmt;
    }

    public IRNode expr() {
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

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("ESEQ");
        stmt.printSExp(p);
        expr.printSExp(p);
        p.endList();
    }
}
