package parser;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
        this.type = Type.INTEGER;
        this.value = value;
    }

    public Value(boolean value) {
        this.children = null; 
        this.etype = Expression.ExpressionType.VALUE;
        this.vtype = Value.ValueType.BOOLEAN;
        this.type = Type.BOOLEAN;
        this.value = value ? TRUE : FALSE;
    }

    public Value(ArrayList<Node> values) {
        assert values.size() > 0;
        this.children = values;
        this.etype = Expression.ExpressionType.VALUE;
        this.vtype = Value.ValueType.ARRAY;
        this.type = new Type(
            values.stream()
                .map(value -> (Value) value)
                .map(value -> value.type)
                .collect(Collectors.toCollection(ArrayList::new))
        );
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
