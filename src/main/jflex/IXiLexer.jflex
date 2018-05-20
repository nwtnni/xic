package lex;

import java.io.FileReader;
import java.io.IOException;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.*;

import util.Filename;
import static parse.IXiSymbol.*;
import xic.XicException;
import lex.LexException.Kind;

%%

%public
%class IXiLexer

%cupsym IXiSymbol
%cup
%yylexthrow LexException

%pack
%unicode
%line
%column

%{
    /* Exposed Interface */

    public static IXiLexer from(String source, String unit) throws XicException {
        String input = Filename.concat(source, unit);
        try {
            IXiLexer lexer = new IXiLexer(new FileReader(input));
            lexer.init(unit, new ComplexSymbolFactory());
            return lexer;
        } catch (IOException e) {
            throw XicException.read(input);
        }
    }

    public ComplexSymbolFactory getSymbolFactory() {
        return symbolFactory;   
    }
    
    public Symbol nextToken() throws LexException {
    	try {
    		return next_token();
    	} catch (IOException io) {
    		throw LexException.internal(Kind.IO);
    	}
    }

    /* JFlex Fields */

    private String unit;
    private ComplexSymbolFactory symbolFactory;

    /* Utility methods */

    private void init(String unit, ComplexSymbolFactory sf) {
        this.unit = unit;
        this.symbolFactory = sf;
    }

    private int row() { return yyline + 1; }

    private int column() { return yycolumn + 1; }

    /* Symbol factory methods */

    private Symbol tokenize(int id) throws LexException {
        Location l = new Location(unit, row(), column());
        Location r = new Location(unit, row(), column() + yylength());
        switch (id) {
            case IDENTIFIER:
                return symbolFactory.newSymbol(yytext(), IDENTIFIER, l, r, yytext());
            default:
                return symbolFactory.newSymbol(yytext(), id, l, r);
        }
    }

    private Symbol throwLexException(int row, int col, Kind kind) throws LexException {
        Location l = new Location(unit, row, col);
        Location r = new Location(unit, row, col + yylength());
        ComplexSymbol symbol = (ComplexSymbol) symbolFactory.newSymbol(yytext(), error, l, r);
        throw new LexException(kind, symbol);
    }
%}

/* main character classes */
EOL = \r|\n|\r\n
InputCharacter = [^\r\n]

Letter = [a-zA-Z]
Digit = [0-9]

Whitespace = [ \t\f]|{EOL}

Comment = "//"{InputCharacter}*{EOL}?

Identifier = {Letter}({Digit}|{Letter}|_|')*

%%

{Whitespace}        { /* ignore */ }
{Comment}           { /* ignore */ }

"use"               { return tokenize(USE); }
"class"             { return tokenize(CLASS); }
"extends"           { return tokenize(EXTENDS); }

// primitive types
"int"               { return tokenize(INT); }
"bool"              { return tokenize(BOOL); }

// symbols
"("                 { return tokenize(LPAREN); }
")"                 { return tokenize(RPAREN); }
"["                 { return tokenize(LBRACK); }
"]"                 { return tokenize(RBRACK); }
"{"                 { return tokenize(LBRACE); }
"}"                 { return tokenize(RBRACE); }
":"                 { return tokenize(COLON); }
","                 { return tokenize(COMMA); }

{Identifier}        { return tokenize(IDENTIFIER); } 

<<EOF>>             { return tokenize(EOF); }
[^]                 { throwLexException(row(), column(), Kind.INVALID_SYNTAX); } 
