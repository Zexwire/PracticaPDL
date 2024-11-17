import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Parser {
	Lexer lexer;
	ParserTable tables;
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
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " se esperaba " + token.getKey());
			switch (action) {
				case ACEPTAR:
					return;
				case DESPLAZAR:
					stack.push(token.getKey());
					stack.push(tables.getGoTo(state, token.getKey()));
					token = lexer.scan();
					break;
				default:
					reduce(state, action);
					parse.add(action.getValue());
			}
		}
	}

	private void reduce(Integer state, Action action) throws ParserException {
		switch (action) {
			case REDUCIR_1: // P -> BP
			case REDUCIR_2: // P -> FP
				stack.pop();
				stack.pop();
			case REDUCIR_3: // P -> eof
				stack.pop();
				stack.pop();
				stack.push(Token.P);
				stack.push(tables.getGoTo(state, Token.P));
				break;
			case REDUCIR_4: //  F  -> function F1 F2 F3 { C }
				for (int i = 0; i < 14; i++)
					stack.pop();
				stack.push(Token.F);
				stack.push(tables.getGoTo(state, Token.F));
				break;
			case REDUCIR_5: // F1 -> T
			case REDUCIR_6: // F1 -> void
				stack.pop();
				stack.pop();
				stack.push(Token.F1);
				stack.push(tables.getGoTo(state, Token.F1));
				break;
			case REDUCIR_7: // F2 -> id
				stack.pop();
				stack.pop();
				stack.push(Token.F2);
				stack.push(tables.getGoTo(state, Token.F2));
				break;
			case REDUCIR_8: // F3 -> ( A )
				for (int i = 0; i < 6; i++)
					stack.pop();
				stack.push(Token.F3);
				stack.push(tables.getGoTo(state, Token.F3));
				break;
			case REDUCIR_9: // A -> T id K
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_10: // A -> void
				stack.pop();
				stack.pop();
				stack.push(Token.A);
				stack.push(tables.getGoTo(state, Token.A));
				break;
			case REDUCIR_11: // K -> , T id K
				for (int i = 0; i < 8; i++)
					stack.pop();
			case REDUCIR_12: // K -> lambda
				stack.push(Token.K);
				stack.push(tables.getGoTo(state, Token.K));
				break;
			case REDUCIR_13: // C -> B C
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_14: // C -> lambda
				stack.push(Token.C);
				stack.push(tables.getGoTo(state, Token.C));
				break;
			case REDUCIR_15: // B -> switch B1 { W }
				stack.pop();
				stack.pop();
			case REDUCIR_16: // B -> var T id ;
				stack.pop();
				stack.pop();
			case REDUCIR_17: // B -> if B1 S
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_18: // B -> S
				stack.pop();
				stack.pop();
				stack.push(Token.B);
				stack.push(tables.getGoTo(state, Token.B));
			case REDUCIR_19: // B1 -> ( E )
				for (int i = 0; i < 6; i++)
					stack.pop();
				stack.push(Token.B1);
				stack.push(tables.getGoTo(state, Token.B1));
			case REDUCIR_20: // T -> int
			case REDUCIR_21: // T -> boolean
			case REDUCIR_22: // T -> string
				stack.pop();
				stack.pop();
				stack.push(Token.T);
				stack.push(tables.getGoTo(state, Token.T));
				break;
			case REDUCIR_23: // W -> case ent : C W
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_24: // W -> default : C
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_25: // W -> lambda
				stack.push(Token.W);
				stack.push(tables.getGoTo(state, Token.W));
				break;
			case REDUCIR_26: // S -> output E ;
			case REDUCIR_27: // S -> input id ;
			case REDUCIR_28: // S -> return X ;
				stack.pop();
				stack.pop();
			case REDUCIR_29: // S -> break ;
			case REDUCIR_30: // S -> id S1
				for (int i = 0; i < 4; i++)
					stack.pop();
				stack.push(Token.S);
				stack.push(tables.getGoTo(state, Token.S));
				break;
			case REDUCIR_31: // S1 -> ( L ) ;
				stack.pop();
				stack.pop();
			case REDUCIR_32: // S1 -> = E ;
				for (int i = 0; i < 6; i++)
					stack.pop();
				stack.push(Token.S1);
				stack.push(tables.getGoTo(state, Token.S1));
				break;
			case REDUCIR_33: // L -> E Q
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_34: // L -> lambda
				stack.push(Token.L);
				stack.push(tables.getGoTo(state, Token.L));
				break;
			case REDUCIR_35: // Q -> , E Q
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_36: // Q -> lambda
				stack.push(Token.Q);
				stack.push(tables.getGoTo(state, Token.Q));
				break;
			case REDUCIR_37: // X -> E
				stack.pop();
				stack.pop();
			case REDUCIR_38: // X -> lambda
				stack.push(Token.X);
				stack.push(tables.getGoTo(state, Token.X));
				break;
			case REDUCIR_39: // E -> E > R
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_40: // E -> R
				stack.pop();
				stack.pop();
				stack.push(Token.E);
				stack.push(tables.getGoTo(state, Token.E));
				break;
			case REDUCIR_41: // R -> R * U
				for (int i = 0; i < 4; i++)
					stack.pop();
			case REDUCIR_42: // R -> U
				stack.pop();
				stack.pop();
				stack.push(Token.R);
				stack.push(tables.getGoTo(state, Token.R));
				break;
			case REDUCIR_43: // U -> ! U
			case REDUCIR_44: // U -> ++ U
				stack.pop();
				stack.pop();
			case REDUCIR_45: // U -> V
				stack.pop();
				stack.pop();
				stack.push(Token.U);
				stack.push(tables.getGoTo(state, Token.U));
				break;
			case REDUCIR_46: // V -> ( E )
				stack.pop();
				stack.pop();
			case REDUCIR_47: // V -> id V1
				stack.pop();
				stack.pop();
			case REDUCIR_48: // V -> ent
			case REDUCIR_49: // V -> cad
			case REDUCIR_50: // V -> true
			case REDUCIR_51: // V -> false
				stack.pop();
				stack.pop();
				stack.push(Token.V);
				stack.push(tables.getGoTo(state, Token.V));
				break;
			case REDUCIR_52: // V1 -> ( L )
				for (int i = 0; i < 6; i++)
					stack.pop();
			case REDUCIR_53: // V1 -> lambda
				stack.push(Token.V1);
				stack.push(tables.getGoTo(state, Token.V1));	
				break;
			default:
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " se esperaba " + action);
		}
	}
}
