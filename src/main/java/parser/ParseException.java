package parser;

import java_cup.runtime.ComplexSymbolFactory.*;

import xic.XicException;

@SuppressWarnings("serial")
public class ParseException extends XicException {

    public ParseException(ComplexSymbol symbol) {
    	super(XicException.Kind.SYNTAX, symbol.getLeft(), "Unexpected token " + symbol.getName());
    }

    private ParseException(String description) {
		super(description);
	}

	private ParseException(Location l, String literal) {
		super(XicException.Kind.SYNTAX, l, literal);
	}
	
	public static ParseException internal(Exception e) {
		return new ParseException("Internal parser error: " + e.toString());
	}

	public static ParseException numberFormatException(Location l, String literal) {
		return new ParseException(l, "Invalid integer literal " + literal);
	}
}