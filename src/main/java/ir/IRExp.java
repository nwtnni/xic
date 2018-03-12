package ir;

import edu.cornell.cs.cs4120.util.SExpPrinter;

/**
 * An intermediate representation for evaluating an expression for side effects,
 * discarding the result
 * EXP(e)
 */
public class IRExp extends IRStmt {
    private IRNode expr;

    /**
     *
     * @param expr the expression to be evaluated and result discarded
     */
    public IRExp(IRNode expr) {
        this.expr = expr;
    }

    public IRNode expr() {
        return expr;
    }

    @Override
    public String label() {
        return "EXP";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRExpr expr = (IRExpr) v.visit(this, this.expr);

    //     if (expr != this.expr) return v.nodeFactory().IRExp(expr);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(expr));
    //     return result;
    // }

    // @Override
    // public CheckCanonicalIRVisitor checkCanonicalEnter(
    //         CheckCanonicalIRVisitor v) {
    //     return v.enterExp();
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("EXP");
        expr.printSExp(p);
        p.endList();
    }
}
