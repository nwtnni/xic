package parser;

public class Bool extends Expression {
    
    private boolean value;
    
    public Bool(boolean value) {
        this.children = null;
        this.kind = Expression.Kind.BOOL;
        this.value = value; 
    }

    public boolean validate() {
        return true; 
    }

    public String toString() {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
