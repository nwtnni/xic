package ir;

/**
 * An intermediate representation for a conditional transfer of control
 * CJUMP(expr, trueLabel, falseLabel)
 */
public class IRCJump extends IRStmt {
    /**
    * cond is IRExpr in release code
    */
    public IRNode cond;
    private String trueName, falseName;
    public IRLabel trueLabel, falseLabel;

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param cond the condition for the jump
     * @param trueName the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRNode cond, String trueName) {
        this(cond, trueName, null);
    }

    /**
     *
     * @param cond the condition for the jump
     * @param trueName the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseName the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRNode cond, String trueName, String falseName) {
        this.cond = cond;
        this.trueName = trueName;
        this.falseName = falseName;
    }

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    public IRCJump(IRNode cond, IRLabel trueLabel) {
        this(cond, trueLabel, null);
    }

    /**
     *
     * @param cond the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    public IRCJump(IRNode cond, IRLabel trueLabel, IRLabel falseLabel) {
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }
    
    public IRNode cond() {
        return cond;
    }

    public String trueName() {
        return (trueLabel != null) ? trueLabel.name() : trueName;
    }

    public String falseName() {
        return (falseLabel != null) ? falseLabel.name() : falseName;
    }

    public boolean hasFalseLabel() {
        return falseLabel != null || falseName != null;
    }

    @Override
    public String label() {
        return "CJUMP";
    }

    // @Override
    // public IRNode visitChildren(IRVisitor v) {
    //     IRExpr expr = (IRExpr) v.visit(this, this.cond);

    //     if (expr != this.cond)
    //         return v.nodeFactory().IRCJump(expr, trueLabel, falseLabel);

    //     return this;
    // }

    // @Override
    // public <T> T aggregateChildren(AggregateVisitor<T> v) {
    //     T result = v.unit();
    //     result = v.bind(result, v.visit(cond));
    //     return result;
    // }

    // @Override
    // public boolean isCanonical(CheckCanonicalIRVisitor v) {
    //     return !hasFalseLabel();
    // }

    @Override
    public <T> T accept(IRVisitor<T> v) {
        return v.visit(this);
    }
}
