package ir;

/**
 * An intermediate representation for a 64-bit integer constant.
 * CONST(n)
 */
public class IRConst extends IRExpr {
    public long value;

    /**
     *
     * @param value value of this constant
     */
    public IRConst(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String label() {
        return "CONST(" + value + ")";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public long constant() {
        return value;
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
