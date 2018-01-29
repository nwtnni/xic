%%

%public
%class MyLexer
/* %type Token
%function nextToken */

%unicode
%pack
%standalone
%line
%column

%{
/*    enum TokenType {
	IF,
	ID,
	INT,
	FLOAT,
	DOT
    }
    class Token {
	TokenType type;
	Object attribute;
	Token(TokenType tt) {
	    type = tt; attribute = null;
	}
	Token(TokenType tt, Object attr) {
	    type = tt; attribute = attr;
	}
	public String toString() {
	    return "" + type + "(" + attribute + ")";
	}
    } */
%}

Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]

Identifier = {Letter}({Digit}|{Letter}|_|')*
Integer = "0"|"-"?[1-9]{Digit}*
EscapeCharacter = \\[tbnrf\'\"\\]
Character = '([^\\\'\"]|{EscapeCharacter})'

Keywords = "use"|"if"|"while"|"return"
Primitives = "int"|"bool"|"true"|"false"

%%

{Whitespace}  { /* ignore */ }
{Identifier}  { System.out.printf("%d:%d id %s\n", yyline,yycolumn, yytext()); }
{Integer}     { System.out.printf("%d:%d integer %s\n", yyline,yycolumn, yytext()); }
{Character}   { System.out.printf("%d:%d character %s\n", yyline,yycolumn, yytext().substring(1,yytext().length()-1)); }
[^]           {System.out.printf("%d:%d, %s ERROR!!! \n",yyline,yycolumn, yytext());}