package parser;

public abstract class Expression extends Node {

    protected Kind kind;

    public enum Kind {
        BOOL,
        BOOLARRAY,
        INDEX,
        INT,
        INTARRAY,
        FUNCTION,
        MULTIPLE, 
        OPERATOR,
        VARIABLE,
    }
    
    public Kind kind() {
        return this.kind; 
    };
}
