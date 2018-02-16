package parser;

import java_cup.runtime.ComplexSymbolFactory.*;

public class ParserError extends Exception {
    public ComplexSymbol symbol;

    public ParserError(ComplexSymbol sym, String msg) {
        super(msg);
        symbol = sym;
    }

    public String toString() {
        String msg = getMessage();
        Location l = symbol.getLeft();
        return String.format("%d:%d error:%s", l.getLine(), l.getColumn(), msg);
    }

}