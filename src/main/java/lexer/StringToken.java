package lexer;

public class StringToken extends Token {
    String value;

    StringToken(int r, int c, String l, String v) {
        super(TokenType.STRING, r, c, l);
        value = v;
    }

    public String toString() {
        return String.format("%d:%d string %s", row, col, literal);
    }
}
