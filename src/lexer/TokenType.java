package lexer;

public enum TokenType {
    USE,
    IF,
    WHILE,
    ELSE,
    RETURN,
    LENGTH,
    INT,
    BOOL,
    TRUE,
    FALSE,
    LNEG,
    NEG,
    MULT,
    HMULT,
    DIV,
    MOD,
    ADD,
    SUB,
    LTE,
    LT,
    GTE,
    GT,
    EQEQ,
    EQ,
    NEQ,
    LAND,
    LOR,
    LPAREN,
    RPAREN,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,
    COLON,
    SEMICOLON,
    COMMA,
    DOT,
    ID ("id"),
    INTEGER ("integer"),
    CHAR ("character"),
    STRING ("string"),
    EOF;
    
    public final String label;

    TokenType() {
        label = null;
    }

    TokenType(String l) {
        label = l;
    }
}
