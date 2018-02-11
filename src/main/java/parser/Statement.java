package parser; 

public abstract class Statement extends Node {

    protected StatementType stype;

    public enum StatementType {
        ASSIGN,
        IF,
        WHILE,
        RETURN,
        PROCEDURE,
        BLOCK,
        DECLARE,
    }

    public StatementType statementType() {
        return this.stype;
    }
}
