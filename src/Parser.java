import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Parser {
	Lexer lexer;
	ParserTable tables;
	//FIXME: añadir segunda parte, con un pair, para los atributos, diría una lista de cosas o un struct
	ArrayDeque<Object> stack;
	ArrayList<Integer> parse;

	public Parser(String filename, TSHandler tsHandler) throws FileNotFoundException, IOException {
		lexer = new Lexer(filename, tsHandler);
		tables = new ParserTable();
		stack = new ArrayDeque<Object>();
		parse = new ArrayList<Integer>();
		stack.push(0);
	}

	public void parse() throws IOException, LexerException, TSException, ParserException {
		Pair<Token, Object> token = lexer.scan();
		while (true) {
			Integer state = (Integer) stack.peek();
			Action action = tables.getAction(state, token.getKey());
			if (action == null)
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " no se esperaba " + token.getKey());
			switch (action) {
				case ACEPTAR:
					toFile("parse.txt");
					return;
				case DESPLAZAR:
					stack.push(token.getKey());
					stack.push(tables.getGoTo(state, token.getKey()));
					token = lexer.scan();
					break;
				default:
					reduce(action);
					//FIXME: parche para que el parser de los numeros correctos hasta cambiar
					//toda la tabla
					parse.add(action.ordinal() - 1);
			}
		}
	}

	private void reduce(Action action) throws ParserException {
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
				state = (Integer) stack.peek();
				stack.push(Token.P);
				stack.push(tables.getGoTo(state, Token.P));
				break;
			case REDUCIR_5: //  F  -> function F1 F2 F3 { C }
				for (int i = 0; i < 14; i++)
					stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.F);
				stack.push(tables.getGoTo(state, Token.F));
				break;
			case REDUCIR_6: // F1 -> T
			case REDUCIR_7: // F1 -> void
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.F1);
				stack.push(tables.getGoTo(state, Token.F1));
				break;
			case REDUCIR_8: // F2 -> id
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.F2);
				stack.push(tables.getGoTo(state, Token.F2));
				break;
			case REDUCIR_9: // F3 -> ( A )
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.F3);
				stack.push(tables.getGoTo(state, Token.F3));
				break;
			case REDUCIR_10: // A -> T id K
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_11: // A -> void
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.A);
				stack.push(tables.getGoTo(state, Token.A));
				break;
			case REDUCIR_12: // K -> , T id K
				for (int i = 0; i < 8; i++)
					stack.pop();
			case REDUCIR_13: // K -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.K);
				stack.push(tables.getGoTo(state, Token.K));
				break;
			case REDUCIR_14: // C -> B C
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_15: // C -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.C);
				stack.push(tables.getGoTo(state, Token.C));
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
				state = (Integer) stack.peek();
				stack.push(Token.B);
				stack.push(tables.getGoTo(state, Token.B));
				break;
			case REDUCIR_20: // B1 -> ( E )
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.B1);
				stack.push(tables.getGoTo(state, Token.B1));
				break;
			case REDUCIR_21: // T -> int
			case REDUCIR_22: // T -> boolean
			case REDUCIR_23: // T -> string
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.T);
				stack.push(tables.getGoTo(state, Token.T));
				break;
			case REDUCIR_24: // W -> case ent : C W
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_25: // W -> default : C
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_26: // W -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.W);
				stack.push(tables.getGoTo(state, Token.W));
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
				state = (Integer) stack.peek();
				stack.push(Token.S);
				stack.push(tables.getGoTo(state, Token.S));
				break;
			case REDUCIR_32: // S1 -> ( L ) ;
				stack.pop();
				stack.pop();
			case REDUCIR_33: // S1 -> = E ;
				for (int i = 0; i < 6; i++)
					stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.S1);
				stack.push(tables.getGoTo(state, Token.S1));
				break;
			case REDUCIR_34: // L -> E Q
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_35: // L -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.L);
				stack.push(tables.getGoTo(state, Token.L));
				break;
			case REDUCIR_36: // Q -> , E Q
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_37: // Q -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.Q);
				stack.push(tables.getGoTo(state, Token.Q));
				break;
			case REDUCIR_38: // X -> E
				stack.pop();
				stack.pop();
			case REDUCIR_39: // X -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.X);
				stack.push(tables.getGoTo(state, Token.X));
				break;
			case REDUCIR_40: // E -> E > R
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_41: // E -> R
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.E);
				stack.push(tables.getGoTo(state, Token.E));
				break;
			case REDUCIR_42: // R -> R * U
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_43: // R -> U
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.R);
				stack.push(tables.getGoTo(state, Token.R));
				break;
			case REDUCIR_44: // U -> ! U
			case REDUCIR_45: // U -> ++ id
				stack.pop();
				stack.pop();
			case REDUCIR_46: // U -> V
				stack.pop();
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.U);
				stack.push(tables.getGoTo(state, Token.U));
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
				stack.pop();
				state = (Integer) stack.peek();
				stack.push(Token.V);
				stack.push(tables.getGoTo(state, Token.V));
				break;
			case REDUCIR_53: // V1 -> ( L )
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_54: // V1 -> lambda
				state = (Integer) stack.peek();	
				stack.push(Token.V1);
				stack.push(tables.getGoTo(state, Token.V1));	
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
