package parse;

import java_cup.runtime.ComplexSymbolFactory.*;

import xic.XicException;

/**
 * Represents all possible syntactic errors.
 */
@SuppressWarnings("serial")
public class ParseException extends XicException {

    /**
     * Default constructor takes in an unexpected parsed symbol.
     */
    public ParseException(ComplexSymbol symbol) {
        super(XicException.Kind.SYNTAX, symbol.getLeft(), "Unexpected token " + symbol.getName());
    }

    /**
     * Takes in a description string to print.
     */
    private ParseException(String description) {
        super(description);
    }

    /**
     * Takes in a source code location and a literal to print.
     */
    private ParseException(Location l, String literal) {
        super(XicException.Kind.SYNTAX, l, literal);
    }

    /**
     * Wrapper for internal parser exceptions.
     */
    public static ParseException internal(Exception e) {
        return new ParseException("Internal parser error: " + e.toString());
    }

    /**
     * Wrapper for literal long conversion exceptions.
     */
    public static ParseException numberFormatException(Location l, String literal) {
        return new ParseException(l, "Invalid integer literal " + literal);
    }
}