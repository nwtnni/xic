package lexer;

import java_cup.runtime.ComplexSymbolFactory.*;
import xic.XicException;

@SuppressWarnings("serial")
public class LexException extends XicException {
	
	public enum Kind {
		
		// IO errors
		IO("Failed to read next token from file"),
		
		// Lex errors
		INVALID_CHAR_LITERAL("Invalid char literal"),
		INVALID_ESCAPE_SEQUENCE("Invalid escape sequence"),
		INVALID_STRING_TERMINATOR("String literal not properly terminated"),
		INVALID_SYNTAX("Invalid syntax");
		
		private String description;
		private Kind(String description) { this.description = description; }
		public String toString() { return description; }
	}
	
    public LexException(Kind kind, ComplexSymbol symbol) {
    	super(XicException.Kind.LEXICAL, symbol.getLeft(), kind.toString());
    }
	
	private LexException(Kind kind) {
		super(kind.toString());
	}

	public static LexException internal(Kind kind) {
		return new LexException(kind);
	}
}