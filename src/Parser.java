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
			//FIXME: Se tiene que cambiar el mensaje de error para que sea más descriptivo, pero no se como encontrar esa informacion
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " no se esperaba " + token.getKey());
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
			case REDUCIR_7: // F1 -> void
				stack.pop();
				aux.add(stack.pop().getValue().get(0));
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F1), null));
				break;
			case REDUCIR_8: // F2 -> id
				stack.pop();
				aux.add(stack.pop().getValue().get(0));
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F2, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F2), null));
				break;
			case REDUCIR_9: // F3 -> ( A )
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.F3, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.F3), null));
				break;
			case REDUCIR_10: // A -> T id K
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_11: // A -> void
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
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.K, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.K), null));
				break;
			case REDUCIR_14: // C -> B C
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_15: // C -> lambda
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
			case REDUCIR_22: // T -> boolean
			case REDUCIR_23: // T -> string
				stack.pop();
				aux.add(stack.pop().getValue().get(0));
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.T, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.T), null));
				break;
			case REDUCIR_24: // W -> case ent : C W
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_25: // W -> default : C
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_26: // W -> lambda
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
				stack.pop();
				stack.pop();
			case REDUCIR_33: // S1 -> = E ;
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.S1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.S1), null));
				break;
			case REDUCIR_34: // L -> E Q
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_35: // L -> lambda
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.L, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.L), null));
				break;
			case REDUCIR_36: // Q -> , E Q
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_37: // Q -> lambda
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.Q, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.Q), null));
				break;
			case REDUCIR_38: // X -> E
				stack.pop();
				stack.pop();
			case REDUCIR_39: // X -> lambda
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.X, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.X), null));
				break;
			case REDUCIR_40: // E -> E > R
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_41: // E -> R
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.E, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.E), null));
				break;
			case REDUCIR_42: // R -> R * U
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_43: // R -> U
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.R, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.R), null));
				break;
			case REDUCIR_44: // U -> ! U
			case REDUCIR_45: // U -> ++ id
				stack.pop();
				stack.pop();
			case REDUCIR_46: // U -> V
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.U, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.U), null));
				break;
			case REDUCIR_47: // V -> ( E )
				stack.pop();
				stack.pop();
			case REDUCIR_48: // V -> id V1
				stack.pop();
				stack.pop();
			case REDUCIR_49: // V -> ent
			case REDUCIR_50: // V -> cad
			case REDUCIR_51: // V -> true
			case REDUCIR_52: // V -> false
				stack.pop();
				aux.add(stack.pop().getValue().get(0));
				state = (Integer) stack.peek().getKey();
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V), null));
				break;
			case REDUCIR_53: // V1 -> ( L )
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_54: // V1 -> lambda
				state = (Integer) stack.peek().getKey();	
				stack.push(new Pair<Object,ArrayList<Object>>(Token.V1, aux));
				stack.push(new Pair<Object,ArrayList<Object>>(tables.getGoTo(state, Token.V1), null));	
				break;
			default:
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " no se esperaba " + action);
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
