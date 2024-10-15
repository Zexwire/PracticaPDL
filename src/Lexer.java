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
		c = readSuperflous();
		if(c == -1)
			return new Pair<Token, Object>(Token.EOF, null);
		else if (Character.isLetter(c))
			return words();
		else if (Character.isDigit(c))
			return cteEntera();
		else if (c == '"')
			return cteCadena();
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
	
	private Pair<Token, Object> cteCadena() throws IOException, LexerException{ //Estado 5 del AFD
			String str = "";
			c = reader.read();
			//FIXME: creo que si estan separadas por \n no son válidas, mirar en documentación
			while(c != '"' && c != -1) {
				//FIXME: comprobar secuencias de escape
				if (c == '\\') {
					if (c == 'n')
						c = '\n';
					else if (c == 't')
						c = '\t';
					else if (c == 'r')
						c = '\r';
					else if (c == 'b')
						c = '\b';
					else if (c == 'f')
						c = '\f';
					else if (c == '"')
						c = '"';
					else if (c == '\\')
						c = '\\';
					else if (c == '0')
						c = '\0';
					else if (c == '\'')
						c = '\'';
					//FIXME: comprobar que tiene sentido(lo ha hecho copilot)
					// y añadir casos que falten 
					else
						throw new LexerException(
							"Caracter no valido en la linea " + lineCount + ": secuencia de escape no valida");
				}
				str = str + c;
				c = reader.read();
			}
			if (c == -1) {
				throw new LexerException("Caracter no valido en la linea " + lineCount + 
											": cadena no cerrada");
			}
			return new Pair<Token, Object>(Token.CteCADENA, str);
	}
	
	private Pair<Token, Object> cteEntera() throws IOException{ //Estado 6 del AFD
		Pair<Token, Object> token = null;
		int n = 0;//Convertir c a su digito
		c = reader.read();
		while(Character.isDigit(c)) {
			n = n*10 + c;//Convertir c a digito
		}
		token = new Pair<Token, Object>(Token.CteENTERA, n);
		return token;
	}
	
}