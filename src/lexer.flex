package lexer;

%%

%public
%class XiLexer
%type Token
%function nextToken

/* %standalone */
%pack

%unicode

%line
%column

%{
    public enum TokenType {
        ID ("id "),
        INT ("integer "),
        CHAR ("character "),
        STRING ("string "),
        SYMBOL (""),
        KEYWORD (""),
        PRIMITIVE (""),
        ERROR ("error:"),
        EOF ("");
        
        private final String msg;
    
        TokenType(String s) {
            this.msg = s;
        }
    }
    public class Token {
        public TokenType type;
        public int col;
        public int row;
        public String value;
        
        private Token(TokenType tt, int r, int c, String v) {
            type = tt;
            row = r;
            col = c;
            value = v;
            System.out.println(toString());
        }

        public String toString() {
            return String.format("%d:%d %s%s", row, col, type.msg, value);
        }
    }

    private Token tokenize(TokenType tt) {
        return new Token(tt, yyline + 1, yycolumn + 1, "");
    }

    private Token tokenize(TokenType tt, String v) {
        return new Token(tt, yyline + 1, yycolumn + 1, v);
    }
%}

/* main character classes*/
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

Letter = [a-zA-Z]
Digit = [0-9]
OctDigit = [0-7]
Hex = {Digit}|[A-Fa-f]

Whitespace = [ \t\f]|{LineTerminator}

Comment = "//" {InputCharacter}* {LineTerminator}?

Identifier = {Letter}({Digit}|{Letter}|_|')*

Integer = "0"|"-"?[1-9]{Digit}*

/* character literals */
EscapeCharacter = \\([tbnrf\'\"\\]|[0-3]?{OctDigit}?{OctDigit}|x{Hex}{2}|u{Hex}{4})
Character = ([^\\\'\"\R\n]|{EscapeCharacter})

CharacterLiteral = '{Character}'

/* string literals */
String = \"{Character}*\"

Operator = [!\*(\*>>)/&\+-<(<=)(>=)>(==)(!=)&\|]
Parenthesis = [\[\]\(\)\{\}]
Punctuation = [;:]
Symbol = {Operator}|{Parenthesis}|{Punctuation}

Keyword = "use"|"if"|"while"|"else"|"return"|"length"
Primitive = "int"|"bool"|"true"|"false"



%%

<YYINITIAL> {
    {Whitespace}        { /* ignore */ }
    {Comment}           { /* ignore */ }
    {Keyword}           { return tokenize(TokenType.KEYWORD, yytext()); }
    {Primitive}         { return tokenize(TokenType.PRIMITIVE, yytext()); } 
    {Identifier}        { return tokenize(TokenType.ID, yytext()); } 
    {Integer}           { return tokenize(TokenType.INT, yytext()); } 
    {CharacterLiteral}  { return tokenize(TokenType.CHAR, yytext().substring(1,yytext().length()-1)); } 
    {String}            { return tokenize(TokenType.STRING, yytext()); } 
    {Symbol}            { return tokenize(TokenType.SYMBOL, yytext()); } 
}
[^]                     { return tokenize(TokenType.ERROR, yytext()); } 
<<EOF>>                 { return tokenize(TokenType.EOF); }
