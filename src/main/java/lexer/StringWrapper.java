package lexer;

import java.util.ArrayList;

public class StringWrapper extends SymbolWrapper {
    public ArrayList<Long> value;

    public StringWrapper(String n, ArrayList<Long> v) {
        super(n);
        value = v;
    }
}