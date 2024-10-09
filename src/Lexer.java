import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer{
    BufferedReader reader;
    TSHandler tsHandler;
    int linea_fichero_fuente;

    public Lexer(String fileName, TSHandler tsHandler) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(fileName));
        this.tsHandler = tsHandler;
        linea_fichero_fuente = 1;
    }

	public Pair<Token, Object> scan() throws LexerException, IOException {
		int c = reader.read();
		//Desde aquí
		while(c != -1 && Character.isWhitespace(c)){ //Estado 0 del AFD
			c = reader.read();
		}
		if(c == -1){
			return new Pair<Token, Object>(Token.EOF, null);
		}
		//TODO:Ver si hay una forma más sencilla de comprobar que el caracter es "/"
		else if(c == "/".codePointAt(0)){ // Paso del estado 0 al 1 del AFD
			comentario(c);
		}
		//Hasta aquí hay que repetirlo cuando se termina de leer un comentario
		//TODO: una función auxiliar?
		switch(c){
			case '*':
				return new Pair<Token, Object>(Token.MULT, null);
			//TODO: completar con los otros tokens
			default:
		}
		//FIXME: placeholder para evitar error de compilación
		return null;
	}
	
	private void comentario(int c) throws LexerException, IOException{ //Inicio: estado 1 del AFD
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
	}
	
	private Pair<Token, Object> words(int c){ //Estado 4 del AFD
		Pair<Token, Object> token = null;
		return token;
	}
	
	private Pair<Token, Object> cteCadena(int c) throws IOException{ //Estado 5 del AFD
		Pair<Token, Object> token = null;
		String str = "";
		c = reader.read();
		while(c != "\"".codePointAt(1)) {
			char[] chars = Character.toChars(c);
			for(char ch: chars) {
				str = str + Character.toString(ch);
			}
		}
		token = new Pair<Token, Object>(Token.CteCADENA, str);
		return token;
	}
	
	private Pair<Token, Object> cteEntera(int c){ //Estado 6 del AFD
		Pair<Token, Object> token = null;
		return token;
	}
	
	private Pair<Token, Object> otherChars(int c) throws LexerException, IOException{ //Estado 0 al 10, estado 11 y manejo de caracteres no validos
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