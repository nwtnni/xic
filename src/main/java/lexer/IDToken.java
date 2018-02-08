package lexer;

public class IDToken extends Token {
    public IDToken(int r, int c, String l) {
        super(TokenType.ID, r, c, l);
    }

    public String toString() {
        return String.format("%d:%d id %s", row, col, literal);
    }
}
