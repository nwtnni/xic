package parser;

public abstract class Expression extends Node {

    protected Type type;
    protected ExpressionType etype;

    public enum ExpressionType {
        INDEX,
        FUNCTION,
        MULTIPLE, 
        OPERATOR,
        VALUE,
        VARIABLE,
    }
    
    public ExpressionType expressionType() {
        return etype; 
    };
}
