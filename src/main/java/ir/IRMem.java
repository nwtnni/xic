package ir;

/**
 * An intermediate representation for a memory location
 * MEM(e)
 */
public class IRMem extends IRExpr {
    public enum MemType {
        NORMAL, IMMUTABLE;

        @Override
        public String toString() {
            switch (this) {
            case NORMAL:
                return "MEM";
            case IMMUTABLE:
                return "MEM_I";
            }
            // Exhaustive switch statement
            assert false;
            return "";
        }
    };

    private MemType memType;
    public IRExpr expr;

    /**
     *
     * @param expr the address of this memory location
     */
    public IRMem(IRExpr expr) {
        this(expr, MemType.NORMAL);
    }

    public IRMem(IRExpr expr, MemType memType) {
        this.expr = expr;
        this.memType = memType;
    }

    public IRNode expr() {
        return expr;
    }

    public MemType memType() {
        return memType;
    }

    @Override
    public String label() {
        return memType.toString();
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRExpr expr = (IRExpr) v.visit(this, this.expr);

    //     if (expr != this.expr) return v.nodeFactory().IRMem(expr);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(expr));
    //     return result;
    // }
    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        return 1013 * (expr.hashCode()) ^ 1009 * (memType.hashCode()); 
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRMem && 
            memType.equals(((IRMem) obj).memType) &&
            expr.equals(((IRMem) obj).expr);
    }
}
