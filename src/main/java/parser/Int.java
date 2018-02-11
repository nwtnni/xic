package parser;

public class Int extends Expression {
    
    private int value;
    
    public Int(int value) {
        this.children = null;
        this.kind = Expression.Kind.INT;
        this.value = value;
    }

    public boolean validate() {
        return true; 
    }

    public String toString() {
        return Integer.toString(value);
    }
}
