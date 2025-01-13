package enums;

public enum Token {
	BOOLEAN,
	BREAK,
	CASE,
	FUNCTION,
	IF,
	INPUT,
	INTEGER,
	OUTPUT,
	RETURN,
	STRING,
	SWITCH,
	VAR,
	VOID,
	AUTOINCREMENTO,
	CteENTERA,
	CteCADENA,
	ID,
	ASIG,
	COMA,
	EOS,
	DosPUNTOS,
	ParentesisABRE,
	ParentesisCIERRA,
	LlaveABRE,
	LlaveCIERRA,
	MULT,
	NOT,
	MAYOR,
	DEFAULT,
	TRUE,
	FALSE,
	EOF,
	P,
	F,
	F1,
	F2,
	F3,
	H,
	A,
	K,
	C,
	B,
	B1,
	W,
	T,
	S,
	S1,
	L,
	Q,
	X,
	E,
	R,
	U,
	V,
	V1;

	@Override
	public String toString() {
		switch (this) {
			case BOOLEAN: return "boolean";
			case BREAK: return "break";
			case CASE: return "case";
			case FUNCTION: return "function";
			case IF: return "if";
			case INPUT: return "input";
			case INTEGER: return "int";
			case OUTPUT: return "output";
			case RETURN: return "return";
			case STRING: return "string";
			case SWITCH: return "switch";
			case VAR: return "var";
			case VOID: return "void";
			case AUTOINCREMENTO: return "++";
			case CteENTERA: return "entero";
			case CteCADENA: return "cadena";
			case ID: return "identificador";
			case ASIG: return "=";
			case COMA: return ",";
			case EOS: return ";";
			case DosPUNTOS: return ":";
			case ParentesisABRE: return "(";
			case ParentesisCIERRA: return ")";
			case LlaveABRE: return "{";
			case LlaveCIERRA: return "}";
			case MULT: return "*";
			case NOT: return "!";
			case MAYOR: return ">";
			case DEFAULT: return "default";
			case TRUE: return "true";
			case FALSE: return "false";
			case EOF: return "EOF";
			default: throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}

	public String getCode() {
		switch (this) {
			case BOOLEAN: return "BOOLEAN";
			case BREAK: return "BREAK";
			case CASE: return "CASE";
			case FUNCTION: return "FUNCTION";
			case IF: return "IF";
			case INPUT: return "INPUT";
			case INTEGER: return "INTEGER";
			case OUTPUT: return "OUTPUT";
			case RETURN: return "RETURN";
			case STRING: return "STRING";
			case SWITCH: return "SWITCH";
			case VAR: return "VAR";
			case VOID: return "VOID";
			case AUTOINCREMENTO: return "AUTOINCREMENTO";
			case CteENTERA: return "CteENTERA";
			case CteCADENA: return "CteCADENA";
			case ID: return "ID";
			case ASIG: return "ASIG";
			case COMA: return "COMA";
			case EOS: return "EOS";
			case DosPUNTOS: return "DosPUNTOS";
			case ParentesisABRE: return "ParentesisABRE";
			case ParentesisCIERRA: return "ParentesisCIERRA";
			case LlaveABRE: return "LlaveABRE";
			case LlaveCIERRA: return "LlaveCIERRA";
			case MULT: return "MULT";
			case NOT: return "NOT";
			case MAYOR: return "MAYOR";
			case DEFAULT: return "DEFAULT";
			case TRUE: return "TRUE";
			case FALSE: return "FALSE";
			case EOF: return "EOF";
			default: throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
