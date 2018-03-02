package lexer;

/**
 * Utility class for symbols. Records
 * the escaped version of a string.
 */
public abstract class SymbolWrapper {
    public String name;
    
    public SymbolWrapper(String n) {
        name = n;
    }
}