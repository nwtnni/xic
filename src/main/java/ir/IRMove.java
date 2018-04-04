package ir;

/**
 * An intermediate representation for a move statement
 * MOVE(target, expr)
 */
public class IRMove extends IRStmt {
    public IRNode target;
    public IRNode src;

    /**
     *
     * @param target the destination of this move
     * @param src the expression whose value is to be moved
     */
    public IRMove(IRNode target, IRNode src) {
        this.target = target;
        this.src = src;
    }

    public IRNode target() {
        return target;
    }

    public IRNode source() {
        return src;
    }

    @Override
    public String label() {
        return "MOVE";
    }
    
    //TODO: is there a better way to do this?
    public boolean isMem() {
        return target instanceof IRMem;
    }
    
    public IRMem getMem() {
        return (IRMem) target;
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRExpr target = (IRExpr) v.visit(this, this.target);
    //     IRExpr expr = (IRExpr) v.visit(this, src);

    //     if (target != this.target || expr != src)
    //         return v.nodeFactory().IRMove(target, expr);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(target));
    //     result = v.bind(result, v.visit(src));
    //     return result;
    // }
    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
