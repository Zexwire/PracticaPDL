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
			case INTEGER: return "integer";
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
			case EOF: return "end of file";
			default: throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
