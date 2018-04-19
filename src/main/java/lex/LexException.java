package lex;

import java_cup.runtime.ComplexSymbolFactory.*;
import xic.XicException;

/**
 * Represents all possible lexical exceptions.
 */
@SuppressWarnings("serial")
public class LexException extends XicException {
    
    /**
     * Enums allow for compile-time assurance that all thrown LexExceptions
     * are drawn from the following set.
     */
    public enum Kind {
        
        // IO errors
        IO("Failed to read next token from file"),
        
        // Lex errors
        INVALID_CHAR("Invalid char literal"),
        INVALID_STRING("Invalid string literal"),
        INVALID_SYNTAX("Invalid syntax");
        
        private String description;
        private Kind(String description) { this.description = description; }
        public String toString() { return description; }
    }
    
    /**
     * Default constructor takes in the kind of error and the associated lexed symbol.
     */
    public LexException(Kind kind, ComplexSymbol symbol) {
        super(XicException.Kind.LEXICAL, symbol.getLeft(), kind.toString());
    }

    /**
     * Takes in the kind of error, without symbol information.
     */
    private LexException(Kind kind) {
        super(kind.toString());
    }

    /**
     * Takes in a kind of internal error.
     */
    public static LexException internal(Kind kind) {
        return new LexException(kind);
    }
}