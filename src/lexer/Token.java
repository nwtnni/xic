package lexer;

public class Token {
    public TokenType type;
    public int col;
    public int row;
    public String literal;
    
    Token(TokenType tt, int r, int c, String l) {
        type = tt;
        row = r;
        col = c;
        literal = l;
    }

    public String toString() {
        String s;
        if (type.label != null) {
            s = String.format("%d:%d %s %s", row, col, type.label, literal);
        } else {
            s = String.format("%d:%d %s", row, col, literal);
        }
        return s;
    }
}
