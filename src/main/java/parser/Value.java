package parser;

import java.util.ArrayList;

public class Value extends Expression {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public enum ValueType {
        INTEGER,
        BOOLEAN,
        ARRAY,
    }
    
    private ValueType vtype;
    private long value;
    
    public Value(long value) {
        this.children = null;
        this.etype = Expression.ExpressionType.VALUE;
        this.vtype = Value.ValueType.INTEGER;
        this.value = value;
    }

    public Value(boolean value) {
        this.children = null; 
        this.etype = Expression.ExpressionType.VALUE;
        this.vtype = Value.ValueType.BOOLEAN;
        this.value = value ? TRUE : FALSE;
    }

    public Value(ArrayList<Node> values) {
        this.children = values;
        this.etype = Expression.ExpressionType.VALUE;
        this.vtype = Value.ValueType.ARRAY;
        this.value = 0;
    }

    public String toString() {
        switch (vtype) {
            case INTEGER: {
                return Long.toString(value);
            }
            case BOOLEAN: {
                return value == TRUE ? "true" : "false";
            }
            case ARRAY: {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (Node child : children) {
                    sb.append(child.toString() + " ");
                }
                sb.append(")");
                return sb.toString();
            }
        }
        assert false;
        return null;
    }
}
