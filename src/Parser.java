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

	private void reduce(Integer state, Action action) {
		//TODO: Implementar las reducciones, recordar meterlas en la lista del parse
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
				break;
			case REDUCIR_14: // C -> lambda
				break;
			case REDUCIR_15: // B -> if B1 S
				break;
			case REDUCIR_16: // B -> var T id ;
				break;
			case REDUCIR_17: // B -> S
				break;
			case REDUCIR_18: // B -> switch B1 { W }
				break;
			case REDUCIR_19: // B1 -> ( E )
				break;
			case REDUCIR_20: // T -> int
				break;
			case REDUCIR_21: // T -> boolean
				break;
			case REDUCIR_22: // T -> string
				break;
			case REDUCIR_23: // W -> case ent : C W
				break;
			case REDUCIR_24: // W -> default : C
				break;
			case REDUCIR_25: // W -> lambda
				break;
			case REDUCIR_26: // S -> id S1
				break;
			case REDUCIR_27: // S -> output E ;
				break;
			case REDUCIR_28: // S -> break ;
				break;
			case REDUCIR_29: // S -> input id ;
				break;
			case REDUCIR_30: // S -> return X ;
				break;
			case REDUCIR_31: // S -> = E ;
				break;
			case REDUCIR_32: // S -> ( L ) ;
				break;
			case REDUCIR_33: // L -> E Q
				break;
			case REDUCIR_34: // L -> lambda
				break;
			case REDUCIR_35: // Q -> , E Q
				break;
			case REDUCIR_36: // Q -> lambda
				break;
			case REDUCIR_37: // X -> E
				break;
			case REDUCIR_38: // X -> lambda
				break;
			case REDUCIR_39: // E -> E > R
				break;
			case REDUCIR_40: // E -> R
				break;
			case REDUCIR_41: // R -> R * U
				break;
			case REDUCIR_42: // R -> U
				break;
			case REDUCIR_43: // U -> ! U
				break;
			case REDUCIR_44: // U -> ++ U
				break;
			case REDUCIR_45: // U -> V
				break;
			case REDUCIR_46: // V -> id V1
				break;
			case REDUCIR_47: // V -> ( E )
				break;
			case REDUCIR_48: // V -> ent
				break;
			case REDUCIR_49: // V -> cad
				break;
			case REDUCIR_50: // V -> true
				break;
			case REDUCIR_51: // V -> false
				break;
			case REDUCIR_52: // V1 -> ( L )
				break;
			case REDUCIR_53: // V1 -> lambda	
				break;
			case REDUCIR_54: // FIXME: me falta una producción
				break;
			default:
				break;
				
		}
	}
}
