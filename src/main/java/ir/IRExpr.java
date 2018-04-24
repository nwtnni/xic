package ir;

import java.util.List;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr extends IRNode {

    protected List<IRNode> values;
    public boolean isCanonical;

    public IRExpr() {
        values = null;
        isCanonical = false;
    }

    public boolean hasValues() {
        return values != null;
    }
    
}
