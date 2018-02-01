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
    private Token currentToken;

    private StringBuilder string = new StringBuilder();

    private int row() { return yyline + 1; };
    
    private int lastColumn = 1;
    
    private int column() {
        switch (yystate()) {
            case YYCHARLITERAL:
            case YYSTRING:
                break;
            default:
                lastColumn = yycolumn + 1;
                break;
        }
        return lastColumn;
    }

    private Token tokenize(TokenType tt) throws Exception {
        String literal;
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
            case NEG:
            case MULT:
            case HMULT:
            case DIV:
            case MOD:
            case ADD:
            case SUB:
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
            case ID:
            case INTEGER:
                literal = yytext();
                break;
            case EOF:
                literal = null;
                break;
            default:
                throw new Exception("Unknown token type.");
        }
        return new Token(tt, row(), column(), literal);
    }

    private Token tokenize(char c) throws Exception {
        String literal = Character.toString(c);
        literal = StringEscapeUtils.escapeJava(literal);
        int col = column();
        yybegin(YYINITIAL);
        return new Token(CHAR, row(), col, literal);
    }

    private Token tokenize(String l) {
        l = StringEscapeUtils.escapeJava(l);
        int col = column();
        yybegin(YYINITIAL);
        return new Token(STRING, row(), col, l);
    }

    private Token logError(String msg) throws Exception {
        throw new Exception(
            String.format("%d:%d error:%s", row(), column(), msg)
        );
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

%state YYSTRING, YYCHARLITERAL

%%

<YYINITIAL> {
    {Whitespace}        { column(); }
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
    // TODO: figure out how to deal with negation NEG
    "*"                 { return tokenize(MULT); }
    "*>>"               { return tokenize(HMULT); }
    "/"                 { return tokenize(DIV); }
    "%"                 { return tokenize(MOD); }
    "+"                 { return tokenize(ADD); }
    "-"                 { return tokenize(SUB); }
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

    \'                  { column(); yybegin(YYCHARLITERAL); }

    \"                  { column(); string.setLength(0); yybegin(YYSTRING); }

}

<YYCHARLITERAL> {
    {SingleChar}\'      { return tokenize(yytext().charAt(0)); }
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
                            int i = Integer.parseInt(yytext().substring(1, yylength() - 1), 8);
                            return tokenize((char) i);
                        }
    {HexEscape}\'       { 
                            int i = Integer.parseInt(yytext().substring(2,4), 16);
                            return tokenize((char) i);
                        }
    \\.                 { logError("Invalid escape sequence \'" + yytext() + "\'"); }
    [^]                 { logError("Invalid character literal"); }
}

<YYSTRING> {
    \"                  { return tokenize(string.toString()); }
    {StringChar}+       { string.append(yytext()); }
    "\\b"               { string.append('\b'); }
    "\\t"               { string.append('\t'); }
    "\\n"               { string.append('\n'); }
    "\\f"               { string.append('\f'); }
    "\\r"               { string.append('\r'); }
    "\\'"               { string.append('\''); }
    "\\\""              { string.append('\"'); }
    "\\\\"              { string.append('\\'); }
    {OctEscape}         { 
                            int i = Integer.parseInt(yytext().substring(1, yylength()), 8);
                            string.append((char) i);
                        }
    {HexEscape}         { 
                            int i = Integer.parseInt(yytext().substring(2,4), 16);
                            string.append((char) i);
                        }
    \\.                 { logError("Invalid escape sequence \"" +  yytext() + "\""); }
    {LineTerminator}    { logError("String literal not properly terminated"); }
}

[^]                     { logError("Invalid syntax"); } 
<<EOF>>                 { return tokenize(EOF); }
