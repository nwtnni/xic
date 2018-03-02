package lexer;

import java.util.ArrayList;

/**
 * Utility class for string symbols. Records
 * the desugared string.
 */
public class StringWrapper extends SymbolWrapper {
    public ArrayList<Long> value;

    public StringWrapper(String n, ArrayList<Long> v) {
        super(n);
        value = v;
    }
}