import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		// Para la primera entrega pediremos desde aquí los tokens
		// al lexer y los pasaremos a un archivo de texto
		ArrayList<Pair<Token, Object>> tokens = new ArrayList<Pair<Token, Object>>();
		TSHandler tsHandler = new TSHandler();

		try {
			Lexer lexer = new Lexer("PIG57.txt", tsHandler);
			Pair<Token, Object> token = lexer.scan();
			while (token.getKey() != Token.EOF && token.getKey() != null) {
				tokens.add(token);
				token = lexer.scan();
			}
			if (token.getKey() == Token.EOF)
				tokens.add(token);
			// FIXME: comprobar que se guardan correctamente los tokens y la tabla de
			// simbolos
			tokensToFile(tokens);
			tsHandler.toFile("tabla_simbolos.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			tokens.clear();
			// TODO: mirar como se hacia para printear el mensaje de la excepción
			e.printStackTrace();
		} catch (LexerException e) {
			tokens.clear();
			// TODO: mirar como se hacia para printear el mensaje de la excepción
			e.printStackTrace();
		} catch (TSException e) {
			e.printStackTrace();
		}
	}

	static private void tokensToFile(ArrayList<Pair<Token, Object>> tokens) {
		try (PrintWriter writer = new PrintWriter("tokens.txt")) {
			for (Pair<Token, Object> tokenPair : tokens) {
				// FIXME: comprobar el toString de tokens
				switch (tokenPair.getKey()) {
					case CteENTERA:
						writer.println("< " + tokenPair.getKey() + ", " + ((Integer) tokenPair.getValue())+ " >\n");
						break;
					case CteCADENA:
						writer.println("< " + tokenPair.getKey() + ", " + ((String) tokenPair.getValue()) + " >\n");
						break;
					case ID:
						writer.println("< " + tokenPair.getKey() + ", " + ((Integer) tokenPair.getValue()) + " >\n");
						break;
					default:
						writer.println("< " + tokenPair.getKey() + ", - >\n");
						break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
