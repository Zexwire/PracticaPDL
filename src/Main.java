import java.util.ArrayList;

public class Main {
	public static void main(String[] args){
		//Para la primera entrega pediremos desde aquí los tokens
		//al lexer y los pasaremos a un archivo de texto
		ArrayList<Pair<Token, Object>> tokens = new ArrayList<Pair<Token, Object>>();
		Lexer lexer = new Lexer("codigo_fuente.txt");
		TSHandler tsHandler = new TSHandler();

		try {
			Pair<Token, Object> token = lexer.scan();
			while(token.getKey() != Token.EOF && token.getKey() != null){
				tokens.add(token);
				token = lexer.scan();
			}
			//TODO: guardar los tokens en un archivo de texto
			//TODO: guardar la tabla de símbolos en un archivo de texto
		} catch (LexerException e) {
			tokens.clear();
			//TODO: mirar como se hacia para printear el mensaje de la excepción
			e.printStackTrace();
		}
	}
}
