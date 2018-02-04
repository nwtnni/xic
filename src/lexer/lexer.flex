package lexer;

import static lexer.TokenType.*;
import org.apache.commons.text.StringEscapeUtils;

%%

%public
%class XiLexer
%type Token
%function nextToken
%yylexthrow Exception

%pack

%unicode

%line
%column

%{
    private StringBuilder value = new StringBuilder();
    private StringBuilder literal = new StringBuilder();

    private int row() { return yyline + 1; }

    private int column() { return yycolumn + 1; }
    
    private int startColumn = 1;

    private Token tokenize(TokenType tt) throws Exception {
        switch (tt) {
            case USE:
            case IF:
            case WHILE:
            case ELSE:
            case RETURN:
            case LENGTH:
            case INT:
            case BOOL:
            case TRUE:
            case FALSE:
            case LNEG:
            case MULT:
            case HMULT:
            case DIV:
            case MOD:
            case ADD:
            case MINUS:
            case LTE:
            case LT:
            case GTE:
            case GT:
            case EQEQ:
            case EQ:
            case NEQ:
            case LAND:
            case LOR:
            case LPAREN:
            case RPAREN:
            case LBRACK:
            case RBRACK:
            case LBRACE:
            case RBRACE:
            case COLON:
            case SEMICOLON:
            case COMMA:
            case DOT:
            case UNDERSCORE:
            case EOF:
                return new Token(tt, row(), column(), yytext());
            case ID:
                return new IDToken(row(), column(), yytext());
            case INTEGER:
                return new IntToken(row(), column(), yytext());
            default:
                throw new Exception("Unknown token type.");
        }
    }

    private Token tokenize(char c) throws Exception {
        yybegin(YYINITIAL);
        String literal = escape(stripQuote(yytext()), c);
        return new CharToken(row(), startColumn, literal, c);
    }

    private Token tokenize() {
        yybegin(YYINITIAL);
        return new StringToken(row(), startColumn, literal.toString(), value.toString());
    }

    private Token logError(int r, int c, String msg) throws Exception {
        throw new Exception(
            String.format("%d:%d error:%s", r, c, msg)
        );
    }

    private String escape(String source, char c) {
        if (!(c <= 0x1F || c == 0x22 || c == 0x27 || 0x5C || 
			  (0x7F <= c && c <= 0x9F))) {
            String s = Character.toString(c);
            return StringEscapeUtils.escapeJava(s);
        }
        return source;
    }

    private String stripQuote(String s) {
        return s.substring(0, s.length() - 1);
    }

%}

/* main character classes*/
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

Letter = [a-zA-Z]
Digit = [0-9]
OctDigit = [0-7]
HexDigit = {Digit}|[A-Fa-f]

Whitespace = [ \t\f]|{LineTerminator}

Comment = "//" {InputCharacter}* {LineTerminator}?

Identifier = {Letter}({Digit}|{Letter}|_|')*

Integer = "0"|[1-9]{Digit}*

SingleChar = [^\r\n\'\\]
StringChar = [^\r\n\"\\]

OctEscape = \\[0-3]?{OctDigit}?{OctDigit}
HexEscape = \\x{HexDigit}{2}
UnicodeEscape = \\u{HexDigit}{4}

%state YYCHARLITERAL, YYSTRING 

%%

<YYINITIAL> {
    {Whitespace}        { /* ignore */ }
    {Comment}           { /* ignore */ }

    // 
    "use"               { return tokenize(USE); }
    "if"                { return tokenize(IF); }
    "while"             { return tokenize(WHILE); }
    "else"              { return tokenize(ELSE); }
    "return"            { return tokenize(RETURN); }
    "length"            { return tokenize(LENGTH); }

    // primatives
    "int"               { return tokenize(INT); }
    "bool"              { return tokenize(BOOL); }
    "true"              { return tokenize(TRUE); }
    "false"             { return tokenize(FALSE); }

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
    "="                 { return tokenize(EQ); }
    "!="                { return tokenize(NEQ); }
    "&"                 { return tokenize(LAND); }
    "|"                 { return tokenize(LOR); }

    // symbols
    "("                 { return tokenize(LPAREN); }
    ")"                 { return tokenize(RPAREN); }
    "["                 { return tokenize(LBRACK); }
    "]"                 { return tokenize(RBRACK); }
    "{"                 { return tokenize(LBRACE); }
    "}"                 { return tokenize(RBRACE); }
    ":"                 { return tokenize(COLON); }
    ";"                 { return tokenize(SEMICOLON); }
    ","                 { return tokenize(COMMA); }
    "."                 { return tokenize(DOT); }
    "_"                 { return tokenize(UNDERSCORE); }

    {Identifier}        { return tokenize(ID); } 

    // TODO: improve integer lexing
    {Integer}           { return tokenize(INTEGER); } 

    // TODO: make lexing chars and strings more robust
    // include custom error messages

    \'                  {
                            startColumn = column();
                            yybegin(YYCHARLITERAL);
                        }

    \"                  {
                            startColumn = column();
                            value.setLength(0);
                            literal.setLength(0);
                            yybegin(YYSTRING);
                        }

}

<YYCHARLITERAL> {
    {SingleChar}\'     { return tokenize(yytext().charAt(0)); }

    // escape sequences
    "\\t"\'            { return tokenize('\t'); }
    "\\b"\'            { return tokenize('\b'); }
    "\\n"\'            { return tokenize('\n'); }
    "\\r"\'            { return tokenize('\r'); }
    "\\f"\'            { return tokenize('\f'); }
    "\\'"\'            { return tokenize('\''); }
    "\\\""\'           { return tokenize('\"'); }
    "\\\\"\'           { return tokenize('\\'); }
    {OctEscape}\'      { 
                            String s = yytext().substring(1, yylength() - 1);
                            char c = (char) Integer.parseInt(s, 8);
                            return tokenize(c);
                        }
    {HexEscape}\'      { 
                            String s = yytext().substring(2, 4);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
                        }
    {UnicodeEscape}\'  {
                            String s = yytext().substring(2, 6);
                            char c = (char) Integer.parseInt(s, 16);
                            return tokenize(c);
                        }
    \\.                 { logError(row(), startColumn, "Invalid escape sequence \'" + yytext() + "\'"); }
    {LineTerminator}    { logError(row(), startColumn, "Character literal not properly terminated"); }
    [^]                 { logError(row(), startColumn, "Invalid character literal"); }
}

<YYSTRING> {
    \"                  { return tokenize(); }
    {StringChar}+       {
                            value.append(yytext());
                            literal.append(yytext());
                        }
    "\\b"               {
                            value.append('\b');
                            literal.append("\\b");
                        }
    "\\t"               {
                            value.append('\t');
                            literal.append("\\t");
                        }
    "\\n"               {
                            value.append('\n');
                            literal.append("\\n");
                        }
    "\\f"               {
                            value.append('\f');
                            literal.append("\\f");
                        }
    "\\r"               {
                            value.append('\r');
                            literal.append("\\r");
                        }
    "\\'"               {
                            value.append('\'');
                            literal.append("\\'");
                        }
    "\\\""              {
                            value.append('\"');
                            literal.append("\\\"");
                        }
    "\\\\"              {
                            value.append('\\');
                            literal.append("\\\\");
                        }
    {OctEscape}         { 
                            String s = yytext().substring(1, yylength());
                            char c = (char) Integer.parseInt(s, 8);
                            value.append(c);
                            literal.append(escape(yytext(), c));
                        }
    {HexEscape}         { 
                            String s = yytext().substring(2, 4);
                            char c = (char) Integer.parseInt(s, 16);
                            value.append(c);
                            literal.append(escape(yytext(), c));
                        }
    {UnicodeEscape}     {
                            String s = yytext().substring(2, 6);
                            char c = (char) Integer.parseInt(s, 16);
                            value.append(c);
                            literal.append(escape(yytext(), c));
                        }
    \\.                 { logError(row(), startColumn, "Invalid escape sequence \"" +  yytext() + "\""); }
    {LineTerminator}    { logError(row(), startColumn, "String literal not properly terminated"); }
    [^]                 { logError(row(), startColumn, "Invalid string literal"); }
}

[^]                     { logError(row(), column(), "Invalid syntax"); } 
<<EOF>>                 { return tokenize(EOF); }
