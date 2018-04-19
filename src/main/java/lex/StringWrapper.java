package lex;

import java.util.List;

/**
 * Utility class for string symbols. Records
 * the desugared string.
 */
public class StringWrapper extends SymbolWrapper {
    public List<Long> value;

    public StringWrapper(String n, List<Long> v) {
        super(n);
        value = v;
    }
}