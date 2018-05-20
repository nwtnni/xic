package ir;

/**
 * An intermediate representation for a memory location
 * MEM(e)
 */
public class IRMem extends IRExpr {
    public enum MemType {
        NORMAL, IMMUTABLE, GLOBAL;

        @Override
        public String toString() {
            switch (this) {
            case NORMAL:
                return "MEM";
            case IMMUTABLE:
                return "MEM_I";
            case GLOBAL:
                return "MEM_G";
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

    public boolean isImmutable() {
        return memType == MemType.IMMUTABLE;
    }

    public boolean isGlobal() {
        return memType == MemType.GLOBAL;
    }

    @Override
    public String label() {
        return memType.toString();
    }

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
