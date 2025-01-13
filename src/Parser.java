import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;

public class Parser {
	private LinkedList<Pair<Object, ArrayList<Object>>> stack;
	private ArrayList<Integer> parse;
	private ParserTable tables;
	private TSHandler tsHandler;
	private Lexer lexer;

	public Parser(String filename, TSHandler tsHandler) throws FileNotFoundException, IOException {
		stack = new LinkedList<Pair<Object, ArrayList<Object>>>();
		parse = new ArrayList<Integer>();
		tables = new ParserTable();
		this.tsHandler = tsHandler;
		lexer = new Lexer(filename, tsHandler);
		stack.push(new Pair<Object, ArrayList<Object>>(0, null));
	}

	public void parse() throws IOException, LexerException, TSException, ParserException {
		Pair<Token, Object> token = lexer.scan();
		while (true) {
			Integer state = (Integer) stack.peek().getKey();
			Action action = tables.getAction(state, token.getKey());
			if (action == null)
				parserError(lexer.getLineCount(), token.getKey(), state);
			// Tenemos que abrir la zona de declaración según los tokens que llegan y no en las reglas
			// debido a que las reglas se pueden aplicar mucho más tarde que se lean los tokens lo cual hace que 
			// por ejemplo si declarasemos var int s; y luego s = 5; se detectaría como error de que s esta doblemente declarado
			if (token.getKey() == Token.VAR || token.getKey() == Token.FUNCTION)
				tsHandler.setDeclarationZone(true);
			else if (token.getKey() == Token.EOS)
				tsHandler.setDeclarationZone(false);
			switch (action) {
				case ACEPTAR:
					toFile("parse.txt");
					lexer.toFile();
					return;
				case DESPLAZAR:
					ArrayList<Object> aux = new ArrayList<Object>();
					// Guardamos la posición del identificador en la tabla de símbolos para futuras comprobaciones
					if (token.getKey() == Token.ID)
						aux.add(token.getValue());
					// Guardamos la línea en la que se ha leído el token para poder mostrarla en caso de error del parser
					aux.add(lexer.getLineCount());
					stack.push(new Pair<Object,ArrayList<Object>>(token.getKey(), aux));
					stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, token.getKey()), null));
					// Como eof es un token en nuestra gramática en el automata se consider aque hay que desplazar pero 
					// si ya se ha leido no hay que volver a leerlo
					if (token.getKey() != Token.EOF)
						token = lexer.scan();
					break;
				default:
					reduce(action);
					// Se tiene que reducir en uno todos los valores de la lista de parse para que coincida con el archivo de salida necesario en VASt
					parse.add(action.ordinal() - 1);
			}
		}
	}

	/**
	 * Función usada para reducir la pila según la regla que se haya obtenido de la tabla de análisis
	 * 
	 * @param action Regla que se va a aplicar
	 * @throws ParserException Error semántico en el código
	 * @throws TSException Error en la tabla de símbolos que se propaga desde TSHandler
	 */
	private void reduce(Action action) throws ParserException, TSException {
		// Listas de atributos auxiliares que se usan tanto para comprobaciones como para guardar los atributos de los no terminales
		ArrayList<Object> atributes = new ArrayList<Object>();
		ArrayList<Object> aux;
		// Variable que se usa para guardar la línea en la que se ha leído el token para mostrarla en caso de error
		Integer actualLineCount;
		Atribute type;

		switch (action) {
			case REDUCIR_2: // P -> BP
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				if (atributes.get(1) != Atribute.EMPTY)
					throw new ParserException("Error en la linea " + (Integer) atributes.get(3) + " hay un return fuera de una función");
				else if ((Boolean) atributes.get(2))
					throw new ParserException("Error en la linea " + (Integer) atributes.get(3) + " hay un break fuera de un switch");
				insertNonTerminal(Token.P, atributes);
				break;
			case REDUCIR_3: // P -> FP
				stack.pop();
				stack.pop();
			case REDUCIR_4: // P -> eof
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.P, atributes);
				break;
			case REDUCIR_5: //  F  -> function F1 F2 F3 { C }
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				for (int i = 0; i < 7; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				if (atributes.get(1) != Atribute.EMPTY && atributes.get(1) != aux.get(1))
					throw new ParserException("Error en la linea " + actualLineCount + " el return de la función no es del tipo correcto");
				else if ((Boolean) atributes.get(2) == true)
					throw new ParserException("Error en la linea " + actualLineCount + " hay un break fuera de un switch");
				insertNonTerminal(Token.F, null);
				tsHandler.closeScope();
				break;
			case REDUCIR_6: // F1 -> T
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.F1, atributes);
				break;
			case REDUCIR_7: // F1 -> void
				stack.pop();
				stack.pop();
				atributes.add(Atribute.NONE);
				insertNonTerminal(Token.F1, atributes);
				break;
			case REDUCIR_8: // F2 -> id
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.F2, atributes);
				tsHandler.openScope();
				break;
			case REDUCIR_9: // F3 -> ( A )
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				stack.pop();
				// Aquí esta justo F2 en segunda posición del stack y F1 en cuarta, con la pos del id de la función y su tipo de retorno
				atributes.add(stack.get(3).getValue().get(0));
				tsHandler.insertFunction((Integer) stack.get(1).getValue().get(0), atributes);
				insertNonTerminal(Token.F3, atributes);
				tsHandler.setDeclarationZone(false);
				break;
			case REDUCIR_10: // A -> T id K
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				insertNonTerminal(Token.A, atributes);
				break;
			case REDUCIR_11: // A -> void
				atributes.add(Atribute.EMPTY);
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.A, atributes);
				break;
			case REDUCIR_12: // K -> , T id K
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.K, atributes);
				break;
			case REDUCIR_13: // K -> lambda
				atributes.add(Atribute.EMPTY);
				insertNonTerminal(Token.K, atributes);
				break;
			case REDUCIR_14: // C -> B C
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				aux = stack.pop().getValue();
				if (aux.get(0) != Atribute.TYPE_OK)
					throw new ParserException("Error en la linea " + lexer.getLineCount() + " hay un error en una sentencia de este cuerpo");
				else if (!((Atribute) atributes.get(1)).equals((Atribute) aux.get(1)) && !((Atribute) atributes.get(1)).equals(Atribute.EMPTY) && !((Atribute) aux.get(1)).equals(Atribute.EMPTY))
					throw new ParserException("Error en la linea " + lexer.getLineCount() + " hay dos returns de distinto tipo en este cuerpo");
				atributes.set(2, (Boolean) atributes.get(2) || (Boolean) aux.get(2));
				insertNonTerminal(Token.C, atributes);
				break;
			case REDUCIR_15: // C -> lambda
				atributes.add(Atribute.TYPE_OK);
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				insertNonTerminal(Token.C, atributes);
				break;
			case REDUCIR_16: // B -> switch B1 { W }
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				if (aux.get(0) != Atribute.ENT)
					throw new ParserException("Error en la linea " + (Integer) aux.get(1) + " la condición del switch no es de tipo entero");
				else if (atributes.get(0) != Atribute.TYPE_OK)
					throw new ParserException("Error en la linea " + actualLineCount + " hay un error en el cuerpo del switch");
				atributes.set(2, false);
				insertNonTerminal(Token.B, atributes);
				break;
			case REDUCIR_17: // B -> var T id ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				stack.pop();
				tsHandler.insertVariable((Integer) aux.get(0), (Atribute) atributes.get(0));
				atributes.clear();
				atributes.add(Atribute.TYPE_OK);
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				insertNonTerminal(Token.B, atributes);
				break;
			case REDUCIR_18: // B -> if B1 S
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				aux = stack.pop().getValue();
				actualLineCount = (Integer) aux.get(1);
				stack.pop();
				stack.pop();
				if (aux.get(0) != Atribute.LOG)
					throw new ParserException("Error en la linea " + actualLineCount + " la condición del if no es de tipo lógico");
				insertNonTerminal(Token.B, atributes);
				break;
			case REDUCIR_19: // B -> S
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.B, atributes);
				break;
			case REDUCIR_20: // B1 -> ( E )
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				insertNonTerminal(Token.B1, atributes);
				break;
			case REDUCIR_21: // T -> int
				stack.pop();
				stack.pop();
				atributes.add(Atribute.ENT);
				insertNonTerminal(Token.T, atributes);
				break;
			case REDUCIR_22: // T -> boolean
				stack.pop();
				stack.pop();
				atributes.add(Atribute.LOG);
				insertNonTerminal(Token.T, atributes);
				break;
			case REDUCIR_23: // T -> string
				stack.pop();
				stack.pop();
				atributes.add(Atribute.CAD);
				insertNonTerminal(Token.T, atributes);
				break;
			case REDUCIR_24: // W -> case ent : C W
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				aux = stack.pop().getValue();
				for (int i = 0; i < 5; i++)
					stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				if (aux.get(0) != Atribute.TYPE_OK)
					throw new ParserException("Error en la linea " + actualLineCount + " hay un error en el cuerpo del case");
				else if (!((Atribute) atributes.get(1)).equals((Atribute) aux.get(1)) && !((Atribute) atributes.get(1)).equals(Atribute.EMPTY) && !((Atribute) aux.get(1)).equals(Atribute.EMPTY))
					throw new ParserException("Error en la linea " + actualLineCount + " hay dos returns de distinto tipo en el switch");
				atributes.set(2, (Boolean) atributes.get(2) || (Boolean) aux.get(2));
				insertNonTerminal(Token.W, atributes);
				break;
			case REDUCIR_25: // W -> default : C
				stack.pop();
				atributes = stack.pop().getValue();
				for (int i = 0; i < 4; i++)
					stack.pop();
				insertNonTerminal(Token.W, atributes);
				break;
			case REDUCIR_26: // W -> lambda
				atributes.add(Atribute.TYPE_OK);
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				insertNonTerminal(Token.W, atributes);
				break;
			case REDUCIR_27: // S -> output E ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				if (atributes.get(0) != Atribute.ENT && atributes.get(0) != Atribute.CAD)
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede aplicar output a un " + ((Atribute) atributes.get(0)).toString());
				atributes.set(0, Atribute.TYPE_OK);
				insertNonTerminal(Token.S, atributes);
				break;
			case REDUCIR_28: // S -> input id ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				type = tsHandler.getType((Integer) atributes.get(0));
				if (type != Atribute.ENT && type != Atribute.CAD)
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede aplicar input a un " + type.toString());
				atributes.set(0, Atribute.TYPE_OK);
				insertNonTerminal(Token.S, atributes);
				break;
			case REDUCIR_29: // S -> return X ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes.add(Atribute.TYPE_OK);
				atributes.add(stack.pop().getValue().get(0));
				atributes.add(false);
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				insertNonTerminal(Token.S, atributes);
				break;
			case REDUCIR_30: // S -> break ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes.add(Atribute.TYPE_OK);
				atributes.add(Atribute.EMPTY);
				atributes.add(true);
				atributes.add(stack.pop().getValue().get(0));
				insertNonTerminal(Token.S, atributes);
				break;
			case REDUCIR_31: // S -> id S1
				stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				atributes = stack.pop().getValue();
				actualLineCount = (Integer) atributes.get(1);
				type = tsHandler.getType((Integer) atributes.get(0));
				if (type.equals(Atribute.FUN) && (Integer) aux.get(0) == -1)
					throw new ParserException("Error en la linea " + actualLineCount + " se ha intentado asignar un valor a la función '" + tsHandler.getLex((Integer) atributes.get(0)));
				else if (!type.equals(Atribute.FUN) && (Integer) aux.get(0) != -1)
					throw new ParserException("Error en la linea " + actualLineCount + " se ha intentado llamar a '" + tsHandler.getLex((Integer) atributes.get(0)) + "' como si fuera una función");
				if (type.equals(Atribute.FUN)) {
					type = tsHandler.getReturnType((Integer) atributes.get(0), aux);
					if (type == null)
						throw new ParserException("Error en la linea " + actualLineCount + " la función '" + tsHandler.getLex((Integer) atributes.get(0)) + "' se ha llamado a la función con parámetros incorrectos");
				} else if (!type.equals((Atribute) aux.get(1)))
					throw new ParserException("Error en la linea " + actualLineCount + " se ha intentado asignar a '" + tsHandler.getLex((Integer) atributes.get(0)) + "' una expresión que no coincide con su tipo");
				atributes.clear();
				atributes.add(Atribute.TYPE_OK);
				atributes.add(Atribute.EMPTY);
				atributes.add(false);
				insertNonTerminal(Token.S, atributes);
				break;
			case REDUCIR_32: // S1 -> ( L ) ;
				for (int i = 0; i < 5; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				atributes.add(0, atributes.size());
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.S1, atributes);
				break;
			case REDUCIR_33: // S1 -> = E ;
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes.add(-1);
				atributes.add(stack.pop().getValue().get(0));
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.S1, atributes);
				break;
			case REDUCIR_34: // L -> E Q
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				insertNonTerminal(Token.L, atributes);
				break;
			case REDUCIR_35: // L -> lambda
				atributes.add(Atribute.EMPTY);
				insertNonTerminal(Token.L, atributes);
				break;
			case REDUCIR_36: // Q -> , E Q
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				atributes.add(0, stack.pop().getValue().get(0));
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.Q, atributes);
				break;
			case REDUCIR_37: // Q -> lambda
				atributes.add(Atribute.EMPTY);
				insertNonTerminal(Token.Q, atributes);
				break;
			case REDUCIR_38: // X -> E
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.X, atributes);
				break;
			case REDUCIR_39: // X -> lambda
				atributes.add(Atribute.NONE); 
				insertNonTerminal(Token.X, atributes);
				break;
			case REDUCIR_40: // E -> E > R
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				if (atributes.get(0) != Atribute.ENT || atributes.get(1) != Atribute.ENT)
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede comparar un " + ((Atribute) atributes.get(0)).toString() + " con un " + ((Atribute) atributes.get(1)).toString());
				atributes.clear();
				atributes.add(Atribute.LOG);
				insertNonTerminal(Token.E, atributes);
				break;
			case REDUCIR_41: // E -> R
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.E, atributes);
				break;
			case REDUCIR_42: // R -> R * U
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				stack.pop();
				atributes.add(stack.pop().getValue().get(0));
				if (!atributes.get(0).equals(Atribute.ENT) || !atributes.get(1).equals(Atribute.ENT))
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede multiplicar un " + ((Atribute) atributes.get(0)).toString() + " con un " + ((Atribute) atributes.get(1)).toString());
				atributes.clear();
				atributes.add(Atribute.ENT);
				insertNonTerminal(Token.R, atributes);
				break;
			case REDUCIR_43: // R -> U
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.R, atributes);
				break;
			case REDUCIR_44: // U -> ! U
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				if (atributes.get(0) != Atribute.LOG)
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede negar un " + ((Atribute) atributes.get(0)).toString());
				insertNonTerminal(Token.U, atributes);
				break;
			case REDUCIR_45: // U -> ++ id
				stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				actualLineCount = (Integer) stack.pop().getValue().get(0);
				type = tsHandler.getType((Integer) atributes.get(0));
				if (type != Atribute.ENT)
					throw new ParserException("Error en la linea " + actualLineCount + " no se puede incrementar un " + type.toString());
				atributes.set(0, Atribute.ENT);
				insertNonTerminal(Token.U, atributes);
				break;
			case REDUCIR_46: // U -> V
				stack.pop();
				atributes = stack.pop().getValue();
				insertNonTerminal(Token.U, atributes);
				break;
			case REDUCIR_47: // V -> ( E )
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.V, atributes);	
				break;
			case REDUCIR_48: // V -> id V1
				stack.pop();
				aux = stack.pop().getValue();
				stack.pop();
				atributes = stack.pop().getValue();
				actualLineCount = (Integer) atributes.get(1);
				type = tsHandler.getType((Integer) atributes.get(0));
				if (((Integer) aux.get(0)) == -1  && type.equals(Atribute.FUN))
					throw new ParserException("Error en la linea " + actualLineCount + " se ha intentado llamar a la función '" + tsHandler.getLex((Integer) atributes.get(0)) + "' sin parámetros");
				else if (((Integer) aux.get(0)) >=0  && !type.equals(Atribute.FUN))
					throw new ParserException("Error en la linea " + actualLineCount + " se ha intentado llamar a '" + tsHandler.getLex((Integer) atributes.get(0)) + "' como si fuera una función");
				if (type.equals(Atribute.FUN))
					type = tsHandler.getReturnType((Integer) atributes.get(0), aux);
				if (type == null)
					throw new ParserException("Error en la linea " + actualLineCount + " la función '" + tsHandler.getLex((Integer) atributes.get(0)) + "' se ha llamado a la función con parámetros incorrectos");
				atributes.clear();
				atributes.add(type);
				insertNonTerminal(Token.V, atributes);
				break;
			case REDUCIR_49: // V -> ent
				stack.pop();
				stack.pop();
				atributes.add(Atribute.ENT);
				insertNonTerminal(Token.V, atributes);
				break;
			case REDUCIR_50: // V -> cad
				stack.pop();
				stack.pop();
				atributes.add(Atribute.CAD);
				insertNonTerminal(Token.V, atributes);
				break;
			case REDUCIR_51: // V -> true
			case REDUCIR_52: // V -> false
				stack.pop();
				stack.pop();
				atributes.add(Atribute.LOG);
				insertNonTerminal(Token.V, atributes);
				break;
			case REDUCIR_53: // V1 -> ( L )
				for (int i = 0; i < 3; i++)
					stack.pop();
				atributes = stack.pop().getValue();
				atributes.add(0, atributes.size());
				stack.pop();
				stack.pop();
				insertNonTerminal(Token.V1, atributes);	
				break;
			case REDUCIR_54: // V1 -> lambda
				atributes.add(-1);
				atributes.add(Atribute.NONE);
				insertNonTerminal(Token.V1, atributes);	
				break;
			default:
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " no se esperaba " + action);
		}
	}

	/**
	 * Función auxiliar para insertar un no terminal en la pila
	 * 
	 * @param token No terminal que se va a insertar
	 * @param atributes Atributos del no terminal
	 */
	private void insertNonTerminal(Token token, ArrayList<Object> atributes) {
		Integer state = (Integer) stack.peek().getKey();
		stack.push(new Pair<Object,ArrayList<Object>>(token, atributes));
		stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, token), null));
	}

	/**
	 * Función auxiliar para reconocer que error sintáctico ha ocurrido.
	 * Como se trabaja con un analizador ascendente se puede saber en que estado se ha producido el error
	 * y que token ha causado el error. Con esta información se puede mostrar un mensaje de error más detallado según el autómata.
	 * 
	 * @param lineCount Línea en la que se ha producido el error
	 * @param token Token que ha causado el error
	 * @param state Estado en el que se ha producido el error
	 * @throws ParserException Error sintáctico en el código
	 */
	private void parserError(int lineCount, Token token, Integer state) throws ParserException {
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
