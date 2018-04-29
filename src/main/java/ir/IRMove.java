package ir;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    public IRExpr target;
    public IRExpr src;

    /**
     *
     * @param target the destination of this move
     * @param src the expression whose value is to be moved
     */
    public IRMove(IRExpr target, IRExpr src) {
        this.target = target;
        this.src = src;
    }

    public IRExpr target() {
        return target;
    }

    public IRExpr src() {
        return src;
    }

    @Override
    public String label() {
        return "MOVE";
    }
    
    public boolean isMem() {
        return target instanceof IRMem;
    }
    
    public IRMem getMem() {
        return (IRMem) target;
    }

    public boolean isTemp() {
        return target instanceof IRTemp;
    }

    public IRTemp getTemp() {
        return (IRTemp) target;
    }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
