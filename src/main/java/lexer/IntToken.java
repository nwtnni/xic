package lexer;

public class IntToken extends Token {
    public IntToken(int r, int c, String l) {
        super(TokenType.INTEGER, r, c, l);
    }

    public String toString() {
        return String.format("%d:%d integer %s", row, col, literal);
    }
}
