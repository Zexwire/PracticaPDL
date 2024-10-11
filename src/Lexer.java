import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer{
    private BufferedReader reader;
    private TSHandler tsHandler;
    private int lineCount;
	private int c;

    public Lexer(String fileName, TSHandler tsHandler) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(fileName));
        this.tsHandler = tsHandler;
        lineCount = 1;
		c = reader.read();
    }

	public Pair<Token, Object> scan() throws LexerException, IOException {
		//Saltar todos los delimitadores
		int c = readSuperflous();
		if(c == -1)
			return new Pair<Token, Object>(Token.EOF, null);
		else if (Character.isLetter(c))
			return words();
		else if (Character.isDigit(c))
			return cteEntera();
		switch (c) {
			case '+':
				c = reader.read();
				if (c == '+') 
					return new Pair<Token, Object>(Token.AUTOINCREMENTO, null);
				else 
					throw new LexerException(
						"Caracter no valido en la linea " + lineCount + " se esperaba un +");
			case '=':
				return new Pair<Token, Object>(Token.ASIG, null);
			case ',':
				return new Pair<Token, Object>(Token.COMA, null);
			case ';':
				return new Pair<Token, Object>(Token.EOS, null);
			case ':':
				return new Pair<Token, Object>(Token.DosPUNTOS, null);
			case '(':
				return new Pair<Token, Object>(Token.ParentesisABRE, null);
			case ')':
				return new Pair<Token, Object>(Token.ParentesisCIERRA, null);
			case '{':
				return new Pair<Token, Object>(Token.LlaveABRE, null);
			case '}':
				return new Pair<Token, Object>(Token.LlaveCIERRA, null);
			case '*':
				return new Pair<Token, Object>(Token.MULT, null);
			case '!':
				return new Pair<Token, Object>(Token.NOT, null);
			case '>':
				return new Pair<Token, Object>(Token.MAYOR, null);
			//TODO: completar con los otros tokens
			default:
		}
		//FIXME: placeholder para evitar error de compilación
		return null;
	}

	private int readSuperflous() throws IOException, LexerException {
		while (c != -1) {
			if (c == '/') {//Estado 1 del AFD
				c = reader.read();
				if(c != '*') 
					throw new LexerException(
						"Caracter no valido en la linea " + lineCount + " se esperaba un *");
				while(c != -1) { //Estado 2 del AFD
					c = reader.read();
					if (c == '\n') {
						++lineCount;
						continue;
					} else if(c == '*') { //Estado 3 del AFD: Lee hasta el ultimo * que encuentre seguido	
						while(c == '*')
							c = reader.read();
						if(c == '/')
							break; //Paso del estado3 al 0:Termina de leer un comentario
					}//Paso del estado 3 al estado 2: Si no ha terminado de leer
				}
			} else if(c == '\n')
				++lineCount;
			else if (!Character.isWhitespace(c))
				return c;//Hemos llegado a un elemento no superfluo
			c = reader.read();
		}
		return c;
	}
	
	private Pair<Token, Object> words() throws IOException{ //Estado 4 del AFD
		String str = "";
		//Podemos poner el '_' de primeras porque ya hemos comprovado que el primero sea una letra
		while (Character.isLetterOrDigit(c) || c == '_') {
			//FIXME: comprobar con caracteres raros
			str = str + c;
			c = reader.read();
		}
		switch (str) {
			case "boolean":
				return new Pair<Token, Object>(Token.BOOLEAN, null);
				case "break":
					return new Pair<Token, Object>(Token.BREAK, null);
			case "case":
				return new Pair<Token, Object>(Token.CASE, null);
			case "function":
				return new Pair<Token, Object>(Token.FUNCTION, null);
			case "if":
				return new Pair<Token, Object>(Token.IF, null);
			case "input":
				return new Pair<Token, Object>(Token.INPUT, null);
			case "integer":
				return new Pair<Token, Object>(Token.INTEGER, null);
			case "output":
				return new Pair<Token, Object>(Token.OUTPUT, null);
			case "return":
				return new Pair<Token, Object>(Token.RETURN, null);
			case "string":
				return new Pair<Token, Object>(Token.STRING, null);
			case "switch":
				return new Pair<Token, Object>(Token.SWITCH, null);
			case "var":
				return new Pair<Token, Object>(Token.VAR, null);
			case "void":
				return new Pair<Token, Object>(Token.VOID, null);
			case "default":
				return new Pair<Token, Object>(Token.DEFAULT, null);
			case "false":
				return new Pair<Token, Object>(Token.FALSE, null);
			case "true":
				return new Pair<Token, Object>(Token.TRUE, null);
			default:
			//ºFIXME: Añadir el caso para el ID
				return null;
		}
	}
	
	private Pair<Token, Object> cteCadena() throws IOException{ //Estado 5 del AFD
		Pair<Token, Object> token = null;
		String str = "";
		c = reader.read();
		while(c != '/') {
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
			default: throw new LexerException(
				"Caracter no valido leido en la linea " + lineCount + ": caracter no perteneciente al lenguaje");
		}
		return token;
	}
	
}