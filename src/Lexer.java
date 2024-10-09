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
		if(c == "*".codePointAt(0)) {
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
	
	
}