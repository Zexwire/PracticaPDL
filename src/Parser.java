import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;

public class Parser {
	Lexer lexer;
	ParserTable tables;
	ArrayDeque<Object> stack;

	public Parser(String filename, TSHandler tsHandler) throws FileNotFoundException, IOException {
		lexer = new Lexer(filename, tsHandler);
		tables = new ParserTable();
		stack = new ArrayDeque<Object>();
		stack.push(0);
	}

	public void parse() throws IOException, LexerException, TSException, ParserException {
		Pair<Token, Object> token = lexer.scan();
		while (true) {
			Action action = tables.getAction((Integer) stack.peek(), token.getKey());
			if (action == null)
				throw new ParserException("Error en la linea " + lexer.getLineCount() + " se esperaba " + token.getKey());
			switch (action) {
				case ACEPTAR:
					return;
				case DESPLAZAR:
					stack.push(token.getKey());
					stack.push(tables.getGoTo((Integer) stack.peek(), token.getKey()));
					token = lexer.scan();
					break;
				default:
					reduce(action);
			}
		}
	}

	private void reduce(Action action) {
		switch ((Integer) stack.peek()) {
			case 1:
		}
	}
}
