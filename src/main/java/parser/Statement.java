package parser; 

public abstract class Statement extends Node {

    protected Kind kind;

    public enum Kind {
        ASSIGN,
        IF,
        WHILE,
        RETURN,
        PROCEDURE,
        BLOCK,
        DECLARE,
    }

    public Kind kind() {
        return this.kind;
    }
}
