%{
/****** Cosas de C (headers, declaraciones, variables, etc) ******/
#include <stdio.h>
#include <stdlib.h>
#include "parser.tab.h"

int length = 0;
int n = 0;
char *str = "";
char *end;
int lastPosTS = 0;

void showError();
void maxIntError();
void tooLongString();
int maxInt(int n);
int maxCadena(int len);

%}
%s IN_COMMENT
%option yylineno
/****** Expresiones regulares ******/


cteEntera ([0-9])+
cteCadena \"([^\n])*\" 
id [a-zA-Z]([a-zA-Z0-9_])*
autoInc [+][+] 
mayor ">>"

%%
"/*"		{ BEGIN(IN_COMMENT);}
<IN_COMMENT>{
[^*\n]		{}
"*"		{}
"\n"		{ yylineno++;}
"*/"		{ BEGIN(INITIAL);}
}
"\n"		{ yylineno++;}
"boolean"	{ return (BOOLEAN);}
"break"		{ return (BREAK);}
"case"		{ return (CASE);}
"function"	{ return (FUNCTION);}
"if"		{ return (IF);}
"input"		{ return (INPUT);}
"int"		{ return (INTEGER);}
"output"	{ return (OUTPUT);}
"return"	{ return (RETURN);}
"string"	{ return (STRING);} 
"switch"	{ return (SWITCH);}
"var"		{ return (VAR);}
"void"		{ return (VOID);}
"default"	{ return (DEFAULT);}
"true"		{ return (TRUE);}
"false"		{ return (FALSE);}
{id}		{ 
			yylval.posicionTS = lastPosTS;
			lastPosTS = lastPosTS + 1;
			return (ID);
		}
{cteEntera}	{
			n = strtol(yytext, &end, 10);
			yylval.n = n;
			if(maxInt(n)==-1)
				maxIntError();
			if(maxInt(n)==-1)
				return (OTHER);
			return (CteEntera);
		}
{cteCadena}	{
			length = yyleng;
			sscanf(yytext, "%s", yylval.cadena);
			if(maxCadena(length) == -1)
				tooLongString();
			if(maxCadena(length) == -1)
				return (OTHER);
			return (CteCadena);
		}
[ \t]		{}
"="		{ return (ASIG);}
","		{ return (COMA);}
";"		{ return (EOS);}
":"		{ return (DosPuntos);}
"("		{ return (ParentesisABRE);}
")"		{ return (ParentesisCIERRA);}
"{"		{ return (LlaveABRE);}
"}"		{ return (LlaveCIERRA);}
"*"		{ return (MULT);}
"!"		{ return (NOT);}
{mayor}		{ return (MAYOR);}
{autoInc}	{ return (AUTOINCREMENTO);}
.		{ showError(); return (OTHER);}

%%

/******* Funciones de C declaradas en Cosas de C ******/

void showError(){
	printf("Caracter inesperado en la linea: %i\n", yylineno);
}

int maxInt(int n){
	if(n > 32767)
		return -1;
	return 0;
}

void maxIntError(){
	printf("Entero mayor que 32767 en la linea: %i\n", yylineno);
}

int maxCadena(int len){
	if(len > 64)
		return -1;
	return 0;
}

void tooLongString(){
	printf("Cadena de mas de 64 caracteres en la linea: %i\n", yylineno);
}

int yywrap(){
	return 1;
}
