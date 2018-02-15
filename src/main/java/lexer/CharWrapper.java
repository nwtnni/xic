package lexer;

public class CharWrapper extends SymbolWrapper {
    public Long value;

    public CharWrapper(String n, Long v) {
        super(n);
        value = v;
    }
}