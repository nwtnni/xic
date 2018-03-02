package lexer;

/**
 * Utility class for string symbols. Records
 * the desugared char.
 */
public class CharWrapper extends SymbolWrapper {
    public Long value;

    public CharWrapper(String n, Long v) {
        super(n);
        value = v;
    }
}