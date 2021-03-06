package lex;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.*;

import util.Filename;
import static parse.XiSymbol.*;
import xic.XicException;
import lex.LexException.Kind;

%%

%public
%class XiLexer

%cupsym XiSymbol
%cup
%yylexthrow LexException

%pack
%unicode
%line
%column

%{
    /* Exposed Interface */

    public static XiLexer from(String source, String unit) throws XicException {
        String input = Filename.concat(source, unit);
        try {
            XiLexer lexer = new XiLexer(new FileReader(input));
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
    private StringBuilder literal = new StringBuilder();
    private ArrayList<Long> value = new ArrayList<Long>();
    private int startColumn = 1;

    /* Utility methods */

    private void init(String unit, ComplexSymbolFactory sf) {
        this.unit = unit;
        this.symbolFactory = sf;
    }

    private int row() { return yyline + 1; }

    private int column() { return yycolumn + 1; }

    private String escape(String source, char c) {
        if (c == 0x08) {
            return "\\b";
        } else if (c == 0x09) {
            return "\\t";
        } else if (c == 0x0A) {
            return "\\n";
        } else if (c == 0x0C) {
            return "\\f";
        } else if (c == 0x0D) {
            return "\\r";
        } else if (c == 0x22) {
            return "\\\"";
        } else if (c == 0x27) {
            return "\\\'";
        } else if (c == 0x5C){
            return "\\\\";
        }
        else if (c <= 0x1F || (0x7F <= c && c <= 0x9F)) {
            return source;
        }
        return Character.toString(c);
    }

    private void buildString(String l, char c) {
        literal.append(l);
        value.add((long) c);
    }

    /* Symbol factory methods */

    private Symbol tokenize(int id) throws LexException {
        Location l = new Location(unit, row(), column());
        Location r = new Location(unit, row(), column() + yylength());
        switch (id) {
            case TRUE:
                return symbolFactory.newSymbol(yytext(), TRUE, l, r, true);
            case FALSE:
                return symbolFactory.newSymbol(yytext(), FALSE, l, r, false);
            case IDENTIFIER:
                return symbolFactory.newSymbol(yytext(), IDENTIFIER, l, r, yytext());
            case INTEGER:
                return symbolFactory.newSymbol(yytext(), INTEGER, l, r, yytext());
            default:
                return symbolFactory.newSymbol(yytext(), id, l, r);
        }
    }

    private Symbol tokenize(char c) {
        yybegin(YYINITIAL);
        Location l = new Location(unit, row(), startColumn);
        Location r = new Location(unit, row(), column());
        String literal = yytext().substring(0, yylength() - 1);      
        String name = escape(literal, c);
        CharWrapper v = new CharWrapper(name, (long) c);
        return symbolFactory.newSymbol(name, CHAR, l, r, v);
    }

    private Symbol tokenize() {
        yybegin(YYINITIAL);
        Location l = new Location(unit, row(), startColumn);
        Location r = new Location(unit, row(), column());
        StringWrapper v = new StringWrapper(literal.toString(), value);
        return symbolFactory.newSymbol(literal.toString(), STRING, l, r, v);
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
OctDigit = [0-7]
HexDigit = {Digit}|[A-Fa-f]

Whitespace = [ \t\f]|{EOL}

Comment = "//"{InputCharacter}*{EOL}?

Identifier = {Letter}({Digit}|{Letter}|_|')*

Integer = "0"|[1-9]{Digit}*

SingleChar = [^\r\n\"\'\\]

OctEscape = \\[0-3]?{OctDigit}?{OctDigit}
HexEscape = \\x{HexDigit}?{HexDigit}
UnicodeEscape = \\u{HexDigit}{4}

%state YYCHARLITERAL, YYSTRING 

%%

<YYINITIAL> {
    {Whitespace}        { /* ignore */ }
    {Comment}           { /* ignore */ }

    // keywords
    "use"               { return tokenize(USE); }
    "if"                { return tokenize(IF); }
    "while"             { return tokenize(WHILE); }
    "else"              { return tokenize(ELSE); }
    "return"            { return tokenize(RETURN); }
    "length"            { return tokenize(LENGTH); }
    "class"             { return tokenize(CLASS); }
    "this"              { return tokenize(THIS); }
    "extends"           { return tokenize(EXTENDS); }
    "break"             { return tokenize(BREAK); }
    "new"               { return tokenize(NEW); }

    // primitives
    "int"               { return tokenize(INT); }
    "bool"              { return tokenize(BOOL); }
    "true"              { return tokenize(TRUE); }
    "false"             { return tokenize(FALSE); }
    "null"              { return tokenize(NULL); }

    // operators
    "!"                 { return tokenize(LNEG); }
    "*"                 { return tokenize(MULT); }
    "*>>"               { return tokenize(HMULT); }
    "/"                 { return tokenize(DIV); }
    "%"                 { return tokenize(MOD); }
    "+"                 { return tokenize(ADD); }
    "-"                 { return tokenize(MINUS); }
    "<="                { return tokenize(LTE); }
    "<"                 { return tokenize(LT); }
    ">="                { return tokenize(GTE); }
    ">"                 { return tokenize(GT); }
    "=="                { return tokenize(EQEQ); }
    "!="                { return tokenize(NEQ); }
    "&"                 { return tokenize(LAND); }
    "|"                 { return tokenize(LOR); }
    "."                 { return tokenize(DOT); }

    // symbols
    "("                 { return tokenize(LPAREN); }
    ")"                 { return tokenize(RPAREN); }
    "["                 { return tokenize(LBRACK); }
    "]"                 { return tokenize(RBRACK); }
    "{"                 { return tokenize(LBRACE); }
    "}"                 { return tokenize(RBRACE); }
    "="                 { return tokenize(EQ); }
    ":"                 { return tokenize(COLON); }
    ";"                 { return tokenize(SEMICOLON); }
    ","                 { return tokenize(COMMA); }
    "_"                 { return tokenize(UNDERSCORE); }

    {Identifier}        { return tokenize(IDENTIFIER); } 

    {Integer}           { return tokenize(INTEGER); } 

    // Character and string literals
    \'                  {
                            startColumn = column();
                            yybegin(YYCHARLITERAL);
                        }

    \"                  {
                            startColumn = column();
                            literal.setLength(0);
                            value = new ArrayList<>();
                            yybegin(YYSTRING);
                        }

}

<YYCHARLITERAL> {
    {SingleChar}\'      { return tokenize(yytext().charAt(0)); }
    \"\'                { return tokenize('\"'); }

    // escape sequences
    "\\t"\'             { return tokenize('\t'); }
    "\\b"\'             { return tokenize('\b'); }
    "\\n"\'             { return tokenize('\n'); }
    "\\r"\'             { return tokenize('\r'); }
    "\\f"\'             { return tokenize('\f'); }
    "\\'"\'             { return tokenize('\''); }
    "\\\""\'            { return tokenize('\"'); }
    "\\\\"\'            { return tokenize('\\'); }
    {OctEscape}\'       { 
                            String s = yytext().substring(1, yylength() - 1);
                            char c = (char) Integer.parseInt(s, 8);
                            return tokenize(c);
                        }
    {HexEscape}\'       { 
                            String s = yytext().substring(2, yylength() - 1);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
                        }
    {UnicodeEscape}\'   {
                            String s = yytext().substring(2, yylength() - 1);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
                        }
    
    {EOL}               { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
    <<EOF>>             { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
    [^]                 { throwLexException(row(), startColumn, Kind.INVALID_CHAR); }
}

<YYSTRING> {
    \"                  { return tokenize(); }
    {SingleChar}        { buildString(yytext(), yytext().charAt(0)); }
    \'                  { buildString("\\\'", '\''); }
    "\\b"               { buildString("\\b", '\b'); }
    "\\t"               { buildString("\\t", '\t'); }
    "\\n"               { buildString("\\n", '\n'); }
    "\\f"               { buildString("\\f", '\f'); }
    "\\r"               { buildString("\\r", '\r'); }
    "\\'"               { buildString("\\\'", '\''); }
    "\\\""              { buildString("\\\"", '\"'); }
    "\\\\"              { buildString("\\\\", '\\'); }
    {OctEscape}         { 
                            String s = yytext().substring(1, yylength());
                            char c = (char) Integer.parseInt(s, 8);
                            buildString(escape(yytext(), c), c);
                        }
    {HexEscape}         { 
                            String s = yytext().substring(2, yylength());
                            char c = (char) Integer.parseInt(s, 16);
                            buildString(escape(yytext(), c), c);
                        }
    {UnicodeEscape}     {
                            String s = yytext().substring(2, yylength());
                            char c = (char) Integer.parseInt(s, 16);
                            buildString(escape(yytext(), c), c);
                        }
    \\                  { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
    {EOL}               { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
    <<EOF>>             { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
    [^]                 { throwLexException(row(), startColumn, Kind.INVALID_STRING); }
}

<<EOF>>                 { return tokenize(EOF); }
[^]                     { throwLexException(row(), column(), Kind.INVALID_SYNTAX); } 
