package parser;

public class Int extends Expression {
    
    private int value;
    
    public Int(int value) {
        this.children = null;
        this.etype = Expression.ExpressionType.INT;
        this.value = value;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
