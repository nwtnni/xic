package ir;

import java.util.List;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr extends IRNode {

    protected List<IRNode> values;

    /** Used in lowering to determine if this expression needs to be hoisted. */
    public boolean isCanonical;

    public IRExpr() {
        values = null;
        isCanonical = false;
    }

    public boolean hasValues() {
        return values != null;
    }

}
