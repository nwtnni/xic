package parser;

public class Bool extends Expression {
    
    private boolean value;
    
    public Bool(boolean value) {
        this.children = null;
        this.etype = Expression.ExpressionType.BOOL;
        this.value = value; 
    }

    public String toString() {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
