package enums;
public enum Atribute {
	ENT,
	CAD,
	LOG,
	FUN,
	NONE,
	EMPTY,
	TYPE_OK,
	TYPE_ERR;

	@Override
	public String toString() {
		switch (this) {
			case ENT:
				return "entero";
			case CAD:
				return "cadena";
			case LOG:
				return "logico";
			case FUN:
				return "función";
			case NONE:
				return "vacio";
			case EMPTY:
				return "-";
			case TYPE_OK:
				return "Type OK";
			case TYPE_ERR:
				return "Type Error";
			default:
				return super.toString();
		}
	}
}
