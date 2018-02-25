package parser;

import java_cup.runtime.ComplexSymbolFactory.*;

import xic.XicException;

@SuppressWarnings("serial")
public class ParseException extends XicException {
	
	private static final String prefix = "unexpected token ";

    public ParseException(ComplexSymbol symbol) {
    	super(XicException.Kind.SYNTAX, symbol.getLeft(), prefix + symbol.getName());
    }

    public ParseException(String description) {
		super(description);
	}
	
	public static ParseException internal(Exception e) {
		return new ParseException("Internal parser error: " + e.toString());
	}
}