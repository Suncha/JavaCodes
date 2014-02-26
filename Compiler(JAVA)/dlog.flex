import java_cup.runtime.*;
%%
%class Lexer
%line
%column
%cup
%{   
  private Symbol symbol(int type) {
  	return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
  	return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
NotOp	= 	[nN][oO][tT]	
Name	= 	[a-z][_A-Za-z0-9]*
Comparison =	[=] | [<] | [>] | [<][>] | [<][=] | [>][=]
Number	=	[0-9]+ | [0-9]+"."[0-9]+ | "."[0-9]+
String 	=	['][^'\r\n]*[']
Variable=	[_A-Z][_A-Za-z0-9]*
%%

<YYINITIAL> {
	"$"		{ return symbol(sym.DOLLAR); }
	":-"	{ return symbol(sym.IMPLIES ); }
	"."		{ return symbol(sym.PERIOD ); }
	","		{ return symbol(sym.COMMA ); }
	"("		{ return symbol(sym.LPAREN ); }
	")"		{ return symbol(sym.RPAREN ); }
	"["		{ return symbol(sym.LBRACKET ); }	
	"]"		{ return symbol(sym.RBRACKET ); }
	":"		{ return symbol(sym.COLON ); }
	"*"		{ return symbol(sym.STAR, new String(yytext())); }
	"#"		{ return symbol(sym.HASH, new String(yytext())); }
	{NotOp}		{ return symbol(sym.NOTOP); }
	{Name}		{ return symbol(sym.NAME, new String(yytext())); }
	{Comparison}	{ return symbol(sym.COMPARISON, new String(yytext()) ); }
	{Number}	{ return symbol(sym.NUMBER, new String(yytext()) ); }
	{String}	{ return symbol(sym.STRING, new String(yytext()) ); }
	{Variable}	{ return symbol(sym.VARIABLE, new String(yytext()) ); }
	{WhiteSpace}	{ /* Do Nothing */ }
}
[^]                    	{ System.out.println("Syntax Error - Scanning Error");
                         return symbol(sym.ERROR); }
