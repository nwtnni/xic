package ir;

import java.util.List;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr extends IRNode {

    // @Override
    // public CheckCanonicalIRVisitor checkCanonicalEnter(
    //         CheckCanonicalIRVisitor v) {
    //     return v.enterExpr();
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return v.inExpr() || !v.inExp();
    // }

    public List<IRNode> values;
    public boolean isCanonical;

    public IRExpr() {
        values = null;
        isCanonical = false;
    }

    public boolean hasValues() {
        return values != null;
    }

    public boolean isConstant() {
        return false;
    }

    public long constant() {
        throw new UnsupportedOperationException();
    }
}
