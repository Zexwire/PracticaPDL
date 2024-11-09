%{
#include <stdio.h>
#include <stdlib.h>

int yylex();
int yyerror(char *s);

%}

/* Aqui declaramos todos los tokens que usara nuestra gramatica */
%token BOOLEAN BREAK CASE FUNCTION IF INPUT INTEGER OUTPUT RETURN STRING SWITCH VAR VOID AUTOINCREMENTO CteEntera CteCadena ID ASIG COMA EOS DosPuntos ParentesisABRE ParentesisCIERRA LlaveABRE LlaveCIERRA MULT NOT MAYOR DEFAULT FALSE TRUE OTHER

/* Aqui se declaran todos los tokens q van acompañados de algo que no sea un null */
%type <n> CteEntera
%type <cadena> CteCadena
%type <posicionTS> ID

/* Sirve de puente entre el lexico y el sintactico y es donde se declaran los tipos */
%union{ 
	int n;
	char *cadena;
	int posicionTS;
}

/* Aqui van las reglas, si una de ella tiene lamba se pone | al principio*/
%%

prog:
    P
;

P:
 B P | F P | YYEOF
;

F:
 FUNCTION F1 F2 F3 LlaveABRE C LlaveCIERRA
;

F1:
  T | VOID
;

F2:
  ID
;

F3:
  ParentesisABRE A ParentesisCIERRA
;

A:
 T ID K | VOID
;

K:
 | COMA T ID K
;

T:
 INTEGER | BOOLEAN | STRING
;

C:
 | B C
;

B:
 IF B1 S | VAR T ID EOS | SWITCH B1 LlaveABRE W LlaveCIERRA | S
;

B1:
  ParentesisABRE E ParentesisCIERRA
;

W:
 | CASE CteEntera DosPuntos C W | DEFAULT DosPuntos C
;

S:
 ID S1 | OUTPUT E EOS | INPUT ID EOS | RETURN X EOS | BREAK EOS
;

S1:
  ASIG E EOS | ParentesisABRE L ParentesisCIERRA EOS
;

L:
 | E Q
;

Q:
 | COMA E Q

X:
 | E
;

E:
 E MAYOR R | R
;

R:
 R MULT U | U
;

U:
 NOT U | AUTOINCREMENTO U | V
;

V:
 ID V1 | ParentesisABRE E ParentesisCIERRA | CteEntera | CteCadena | TRUE | FALSE
;

V1:
  | ParentesisABRE L ParentesisCIERRA
;

%%

int yyerror(char *s){
	extern int yylineno;
	printf("Error sintactico en la linea: %i ,%s\n", yylineno, s);
	return 0;
}

int main(int argc, char *argv[]){
	extern FILE *yyin;
	if(argc > 2){
        	printf("Demasiados argumentos en la linea de invocacion\n");
        	exit(EXIT_FAILURE);
    	}
    	else if(argc == 1){
        	yyin = stdin;
    	}
    	else{
        	FILE *file = fopen(argv[1],"r");
        	if(file == NULL)
        	{
            		printf("File not found\n");
            		exit(EXIT_FAILURE);
        	}
        	else
			yyin = file;
    		}
    	yyparse();
	printf("Todo el fichero leido\n");
    	return 0;
}
