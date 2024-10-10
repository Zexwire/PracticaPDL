import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer{
    private BufferedReader reader;
    private TSHandler tsHandler;
    private int linea_fichero_fuente;
    private int c;

    public Lexer(String fileName, TSHandler tsHandler) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(fileName));
        this.tsHandler = tsHandler;
        linea_fichero_fuente = 1;
        c = reader.read();
    }

	public Pair<Token, Object> scan() throws LexerException, IOException {
		Pair<Token, Object> token = whiteSpaces();
		if(token != null){
			return token;
		}
		//TODO:Ver si hay una forma más sencilla de comprobar que el caracter es "/"
		else if(c == "/".codePointAt(0)){ // Paso del estado 0 al 1 del AFD
			comentario();
		}
		switch(c){
			case '*':
				return new Pair<Token, Object>(Token.MULT, null);
			//TODO: completar con los otros tokens
			default:
		}
		//FIXME: placeholder para evitar error de compilación
		return token;
	}
	
	private Pair<Token, Object> whiteSpaces() throws IOException { //Estado 0 del AFD y EOF
		while(c != -1 && Character.isWhitespace(c)){ 
			c = reader.read();
		}
		if(c == -1){
			return new Pair<Token, Object>(Token.EOF, null);
		}
		return null;
	}
	
	private Pair<Token, Object> comentario() throws LexerException, IOException{ //Inicio: estado 1 del AFD
		//Si no viene un * después de un /
		c = reader.read();
		if(c != "*".codePointAt(0)) {
			throw new LexerException("Caracter no valido en la linea " + linea_fichero_fuente 
										+ " se esperaba un *");
		}
		else { //Estado 2 del AFD
			boolean end = false;
			while(!end) { //Estado 2 del AFD
				c = reader.read();
				if(c == "*".codePointAt(0)) { //Estado 3 del AFD: Lee hasta el ultimo * que encuentre seguido	
					while(c == "*".codePointAt(0)) {
						c = reader.read();
					}
					if(c == "/".codePointAt(0)) end = true; //Paso del estado3 al 0:Termina de leer un comentario
				}//Paso del estado 3 al estado 2: Si no ha terminado de leer
			}
		}
		Pair<Token, Object> token = whiteSpaces();
		if(token != null){
			return token;
		}
		return token;
	}
	
	private Pair<Token, Object> words(){ //Estado 4 del AFD
		Pair<Token, Object> token = null;
		String str = "";
		for(char ch: Character.toChars(c)) {
			str = str + ch;
		}
		
		return token;
	}
	
	private Pair<Token, Object> cteCadena() throws IOException{ //Estado 5 del AFD
		Pair<Token, Object> token = null;
		String str = "";
		c = reader.read();
		while(c != "\"".codePointAt(0)) {
			char[] chars = Character.toChars(c);
			for(char ch: chars) {
				str = str + Character.toString(ch);
			}
		}
		token = new Pair<Token, Object>(Token.CteCADENA, str);
		return token;
	}
	
	private Pair<Token, Object> cteEntera(){ //Estado 6 del AFD
		Pair<Token, Object> token = null;
		return token;
	}
	
	private Pair<Token, Object> otherChars() throws LexerException, IOException{ //Estado 0 al 10, estado 11 y manejo de caracteres no validos
		Pair<Token, Object> token = null;
		//Si no se puede con un switch se hace con if seguidos y ya
		switch(c) {
		//FIXME: Añadir los casos para la c
			case 1: break;
			default: throw new LexerException("Caracter no valido leido en la linea " + linea_fichero_fuente 
												+ ": caracter no perteneciente al lenguaje");
		}
		return token;
	}
	
}