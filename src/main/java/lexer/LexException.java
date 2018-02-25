package lexer;

import java_cup.runtime.ComplexSymbolFactory.*;
import xic.XicException;

@SuppressWarnings("serial")
public class LexException extends XicException {
	
	public enum Kind {
		
		// IO errors
		IO("Failed to read next token from file"),
		
		// Lex errors
		INVALID_CHAR_LITERAL("invalid char literal"),
		INVALID_INT_LITERAL("invalid int literal"),
		INVALID_ESCAPE_SEQUENCE("invalid escape sequence"),
		INVALID_STRING_TERMINATOR("string literal not properly terminated"),
		INVALID_SYNTAX("invalid syntax");
		
		private String description;
		private Kind(String description) { this.description = description; }
		public String toString() { return description; }
	}
	
	public LexException(Kind kind) {
		super(kind.toString());
	}
	
    public LexException(ComplexSymbol symbol, Kind kind) {
    	super(XicException.Kind.LEXICAL, symbol.getLeft(), kind.toString());
    }
}