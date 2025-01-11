import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;

public class Parser {
	Lexer lexer;
	ParserTable tables;
	LinkedList<Pair<Object, ArrayList<Object>>> stack;
	ArrayList<Integer> parse;

	public Parser(String filename, TSHandler tsHandler) throws FileNotFoundException, IOException {
		lexer = new Lexer(filename, tsHandler);
		tables = new ParserTable();
		stack = new LinkedList<Pair<Object, ArrayList<Object>>>();
		parse = new ArrayList<Integer>();
		stack.push(new Pair<Object, ArrayList<Object>>(0, null));
	}

	public void parse() throws IOException, LexerException, TSException, ParserException {
		Pair<Token, Object> token = lexer.scan();
		while (true) {
			Integer state = (Integer) stack.peek().getKey();
			Action action = tables.getAction(state, token.getKey());
			if (action == null)
				parserError(lexer.getLineCount(), token.getKey(), state);
			switch (action) {
				case ACEPTAR:
					toFile("parse.txt");
					return;
				case DESPLAZAR:
				//FIXME: comprobar que no explota, es para los casos de id
					ArrayList<Object> aux = new ArrayList<Object>();
					aux.add(token.getValue());
					stack.push(new Pair<Object,ArrayList<Object>>(token.getKey(), (token.getKey() == Token.ID) ? aux : null));
					stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, token.getKey()), null));
					token = lexer.scan();
					break;
				default:
					reduce(action);
					// Se tiene que reducir en uno todos los valores de la lista de parse para que coincida con el archivo de salida necesario en VASt
					parse.add(action.ordinal() - 1);
			}
		}
	}

	private void reduce(Action action) throws ParserException {
		ArrayList<Object> aux = new ArrayList<Object>();
		Integer state;

		switch (action) {
			//FIXME: Hay que reducir en 1 todas las reglas ya que no se cuenta el
			//axioma inducido como regla
			case REDUCIR_2: // P -> BP
			case REDUCIR_3: // P -> FP
				stack.pop();
				stack.pop();
			case REDUCIR_4: // P -> eof
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.P, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.P), null));
				break;
			case REDUCIR_5: //  F  -> function F1 F2 F3 { C }
				for (int i = 0; i < 14; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F), null));
				break;
			case REDUCIR_6: // F1 -> T
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F1), null));
				break;
			case REDUCIR_7: // F1 -> void
				stack.pop();
				stack.pop();
				aux.add(Atribute.NONE);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F1), null));
				break;
			case REDUCIR_8: // F2 -> id
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F2, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F2), null));
				break;
			case REDUCIR_9: // F3 -> ( A )
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F3, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F3), null));
				break;
			case REDUCIR_10: // A -> T id K
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_11: // A -> void
				aux.add(Atribute.EMPTY);
				aux.add(0);
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.A, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.A), null));
				break;
			case REDUCIR_12: // K -> , T id K
				for (int i = 0; i < 8; i++)
					stack.pop();
			case REDUCIR_13: // K -> lambda
				aux.add(Atribute.EMPTY);
				aux.add(0);
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.K, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.K), null));
				break;
			case REDUCIR_14: // C -> B C
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.C, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.C), null));
				break;
			case REDUCIR_15: // C -> lambda
				aux.add(Atribute.TYPE_OK);
				aux.add(Atribute.EMPTY);
				aux.add(false);
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.C, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.C), null));
				break;
			case REDUCIR_16: // B -> switch B1 { W }
				stack.pop();
				stack.pop();
			case REDUCIR_17: // B -> var T id ;
				stack.pop();
				stack.pop();
			case REDUCIR_18: // B -> if B1 S
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_19: // B -> S
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.B, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.B), null));
				break;
			case REDUCIR_20: // B1 -> ( E )
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.B1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.B1), null));
				break;
			case REDUCIR_21: // T -> int
				stack.pop();
				stack.pop();
				aux.add(Atribute.ENT);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.T, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_22: // T -> boolean
				stack.pop();
				stack.pop();
				aux.add(Atribute.LOG);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.T, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_23: // T -> string
				stack.pop();
				stack.pop();
				aux.add(Atribute.CAD);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.T, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_24: // W -> case ent : C W
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.W, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.W), null));
				break;
			case REDUCIR_25: // W -> default : C
				stack.pop();
				aux = stack.pop().getValue();
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.W, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.W), null));
				break;
			case REDUCIR_26: // W -> lambda
				aux.add(Atribute.TYPE_OK);
				aux.add(Atribute.EMPTY);
				aux.add(false);
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.W, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.W), null));
				break;
			case REDUCIR_27: // S -> output E ;
			case REDUCIR_28: // S -> input id ;
			case REDUCIR_29: // S -> return X ;
				stack.pop();
				stack.pop();
			case REDUCIR_30: // S -> break ;
			case REDUCIR_31: // S -> id S1
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.S, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.S), null));
				break;
			case REDUCIR_32: // S1 -> ( L ) ;
				for (int i = 0; i < 5; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.S1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.S1), null));
				break;
			case REDUCIR_33: // S1 -> = E ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				aux.add(-1);
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.S1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.S1), null));
				break;
			case REDUCIR_34: // L -> E Q
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.L, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.L), null));
				break;
			case REDUCIR_35: // L -> lambda
				aux.add(Atribute.EMPTY);
				aux.add(0);
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.L, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.L), null));
				break;
			case REDUCIR_36: // Q -> , E Q
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.Q, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.Q), null));
				break;
			case REDUCIR_37: // Q -> lambda
				aux.add(Atribute.EMPTY);
				aux.add(0);
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.Q, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.Q), null));
				break;
			case REDUCIR_38: // X -> E
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.X, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.X), null));
				break;
			case REDUCIR_39: // X -> lambda
				aux.add(Atribute.NONE); 
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.X, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.X), null));
				break;
			case REDUCIR_40: // E -> E > R
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.E, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.E), null));
				break;
			case REDUCIR_41: // E -> R
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.E, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.E), null));
				break;
			case REDUCIR_42: // R -> R * U
				for (int i = 0; i < 4; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.R, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.R), null));
				break;
			case REDUCIR_43: // R -> U
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.R, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.R), null));
				break;
			case REDUCIR_44: // U -> ! U
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.U, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.U), null));
				break;
			case REDUCIR_45: // U -> ++ id
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.U, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.U), null));
				break;
			case REDUCIR_46: // U -> V
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.U, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.U), null));
				break;
			case REDUCIR_47: // V -> ( E )
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V1), null));	
				break;
			case REDUCIR_48: // V -> id V1
				stack.pop();
				stack.pop();
				stack.pop();
				aux = stack.pop().getValue();
				state = (Integer) stack.peek().getKey();
				//TODO: comprobaciones	
				break;
			case REDUCIR_49: // V -> ent
				stack.pop();
				stack.pop();
				aux.add(Atribute.ENT);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_50: // V -> cad
				stack.pop();
				stack.pop();
				aux.add(Atribute.CAD);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_51: // V -> true
			case REDUCIR_52: // V -> false
				stack.pop();
				stack.pop();
				aux.add(Atribute.LOG);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_53: // V1 -> ( L )
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V1), null));	
				break;
			case REDUCIR_54: // V1 -> lambda
				aux.add(Atribute.EMPTY);
				aux.add(-1);
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V1), null));	
				break;
			default:
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " no se esperaba " + action);
		}
	}

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

	public void toFile(String fileName) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			writer.print("Ascendente");
			for (int i = 0; i < parse.size(); i++) {
				writer.print(" " + parse.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
