package parser;

public abstract class Expression extends Node {

    protected ExpressionType etype;

    public enum ExpressionType {
        ARRAY,
        BOOL,
        INDEX,
        INT,
        FUNCTION,
        MULTIPLE, 
        OPERATOR,
        VARIABLE,
    }
    
    public ExpressionType expressionType() {
        return etype; 
    };
}
