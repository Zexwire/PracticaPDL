
public class ErrorHandler {
	public void parserError(int lineCount, Token token, Integer state) throws ParserException {
		switch (state) {
			case 0:
			case 2:
			case 3:
			case 82:
			case 86:
			case 90:
			case 91:
			case 99:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en este ámbito.");
			case 1:
			case 4:
			case 15:
			case 16:
				throw new ParserException("Error en la línea " + lineCount + ": Se esperaba el final del fichero no un " + token.toString());
			case 5:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la declaración de un if.");
			case 6:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la declaración de una variable, solo su tipo.");
			case 7:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la declaración de un switch.");
			case 8:
			case 24:
			case 42:
			case 46:
			case 52:
			case 60:
			case 61:
			case 69:
			case 79:
			case 80:
			case 83:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de una sentencia.");
			case 9:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de este identificador, se esperaba o una asignación o una llamada a función.");
			case 10:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " despues de un output.");
			case 11:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un input, solo identificadores.");
			case 12:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un return, se espera o una expresión o ;.");
			case 13:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un break, solo ;.");
			case 14:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un function, sino el tipo de la función.");
			case 17:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un un if, solo una sentencia sentencia simple.");
			case 18:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " dentro del paréntesis, solamente expresiones.");
			case 19:
			case 20:
			case 21:
			case 22:
			case 43:
			case 44:
			case 45:
			case 89:
			case 103:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después del tipo en la declaración, solo un identificador.");
			case 23:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de la declaración del switch, solo {.");
			case 25:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en una asignación, solo expresiones.");
			case 26:
			case 58:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en una llamada a función, solo expresión o ).");
			case 27:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la expresión del output, solo > o ;.");
			case 28:
			case 29:
			case 30:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 41:
			case 47:
			case 49:
			case 51:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 59:
			case 72:
			case 73:
			case 74:
			case 76:
			case 84:
			case 85:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la expresión o su cierre.");
			case 31:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un ++, solo identificadores.");
			case 39:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " al final de un input, solo ;.");
			case 40:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " al final de un return, solo ;.");
			case 48:
			case 97:
			case 98:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en el ámbito del switch, solo case, default o }.");
			case 50:
			case 93:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para finalizar la llamada de una función, solo ).");
			case 62:
			case 63:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " despues del identificador de función, solo (.");
			case 64:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " al final de una declaración de variable, solo ;.");
			case 65:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de la expresión, solo sentencias simples o {.");
			case 66:
			case 92:
			case 102:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para cerrar el switch solo }.");
			case 67:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un case, solo constantes enteras.");
			case 68:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de un default, solo :.");
			case 70:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de la llamada a la función, solo ;.");
			case 71:
			case 75:
			case 100:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para acabar los parámetros de la llamada a la función, solo ).");
			case 77:
			case 95:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para abrir el cuerpo de la función, solo {.");
			case 78:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " al declarar los parámetros de la función, solo el tipo del primer parámetro o void.");
			case 81:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para cerrar el case, solo :.");
			case 87:
			case 88:
			case 105:		
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " después de la declaración de parámetros de la función, solo ).");
			case 94:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " para cerrar la función, solo }.");
			case 96:
			case 104:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " en la declaración de parámetros de la función, solo , para declarar más o ).");
			case 101:
				throw new ParserException("Error en la línea " + lineCount + ": No se esperaba un " + token.toString() + " al declarar un parámetro de la función, solo su tipo.");
		}
	}
}
