package lexer;

import lexer.TokenType;
import static lexer.TokenType.*;

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
    private Token tokenize(TokenType tt) throws Exception {
        String literal;
        switch (tt) {
            case USE: case IF: case WHILE: case ELSE: case RETURN: case LENGTH:
            case INT: case BOOL: case TRUE: case FALSE:
            case LNEG: case NEG:
            case MULT: case HMULT: case DIV: case MOD:
            case ADD: case SUB:
            case LTE: case LT: case GTE: case GT:
            case EQEQ: case EQ: case NEQ:
            case LAND: case LOR:
            case LPAREN: case RPAREN: 
            case LBRACK: case RBRACK:
            case LBRACE: case RBRACE:
            case COLON: case SEMICOLON: case COMMA: case DOT:
            case ID:
                literal = yytext();
                break;
            case INTEGER:
                literal = yytext();j
                break;
            case EOF: 
                literal = null;
                break;
            default:    throw new Exception("Unknown token type.");
        }
        return new Token(tt, yyline + 1, yycolumn + 1, literal);
    }

    private Token tokenize(TokenType tt, String l) {
        return new Token(tt, yyline + 1, yycolumn + 1, l);
    }

    private Token logError(String msg) throws Exception {
        throw new Exception(
            String.format("%d:%d error:%s", yyline + 1, yycolumn + 1, msg)
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

Integer = "0"|"-"?[1-9]{Digit}*

/* character literals */
EscapeCharacter = \\([tbnrf\'\"\\]|[0-3]?{OctDigit}?{OctDigit}|x{HexDigit}{2})
Character = ([^\\\'\"\R\n]|{EscapeCharacter})

CharacterLiteral = '{Character}'

/* string literals */
String = \"{Character}*\"


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

    {Identifier}        { return tokenize(ID); } 

    // TODO: improve integer lexing
    {Integer}           { return tokenize(INTEGER); } 

    // TODO: make lexing chars and strings more robust
    // include custom error messages
    {CharacterLiteral}  { return tokenize(CHAR, yytext()); }
    {String}            { return tokenize(STRING, yytext()); } 
}
[^]                     { logError("invalid syntax"); } 
<<EOF>>                 { return tokenize(EOF); }
