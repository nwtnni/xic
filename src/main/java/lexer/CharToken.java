package lexer;

public class CharToken extends Token {
    char value;

    CharToken(int r, int c, String l, char v) {
        super(TokenType.CHAR, r, c, l);
        value = v;
    }

    public String toString() {
        return String.format("%d:%d character %s", row, col, literal);
    }
}
