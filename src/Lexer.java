import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import enums.Token;
import exceptions.LexerException;
import exceptions.TSException;

public class Lexer {
	private ArrayList<Pair<Token, Object>> tokens;
	private BufferedReader reader;
	private TSHandler tsHandler;
	private int lineCount;
	private int c;

	public Lexer(String fileName, TSHandler tsHandler) throws FileNotFoundException, IOException {
		tokens = new ArrayList<Pair<Token, Object>>();
		reader = new BufferedReader(new FileReader(fileName));
		this.tsHandler = tsHandler;
		lineCount = 1;
		c = reader.read();
	}

	public Pair<Token, Object> scan() throws LexerException, IOException, TSException {
		Pair<Token, Object> tokenPair;
		
		c = readSuperflous();
		if (c == -1) {
			tokenPair = new Pair<Token, Object>(Token.EOF, null);
		} else if (Character.isLetter(c)) {
			tokenPair = words();
		} else if (Character.isDigit(c)) {
			tokenPair = cteEntera();
		} else if (c == '"') {
			tokenPair = cteCadena();
		} else {
			switch (c) {
				case '+':
					c = reader.read();
					if (c == '+') {
						c = reader.read();
						tokenPair = new Pair<Token, Object>(Token.AUTOINCREMENTO, null);
					} else {
						throw new LexerException("Caracter no valido en la linea " + lineCount + " se esperaba un +");
					}
					break;
				case '=':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.ASIG, null);
					break;
				case ',':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.COMA, null);
					break;
				case ';':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.EOS, null);
					break;
				case ':':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.DosPUNTOS, null);
					break;
				case '(':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.ParentesisABRE, null);
					break;
				case ')':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.ParentesisCIERRA, null);
					break;
				case '{':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.LlaveABRE, null);
					break;
				case '}':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.LlaveCIERRA, null);
					break;
				case '*':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.MULT, null);
					break;
				case '!':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.NOT, null);
					break;
				case '>':
					c = reader.read();
					tokenPair = new Pair<Token, Object>(Token.MAYOR, null);
					break;
				default:
					throw new LexerException("Caracter no valido en la linea " + lineCount + ": caracter no reconocido");
			}
		}
		tokens.add(tokenPair);
		return tokenPair;
	}

	private int readSuperflous() throws IOException, LexerException {
		while (c != -1) {
			if (c == '/') {// Estado 1 del AFD
				c = reader.read();
				if (c != '*')
					throw new LexerException("Caracter no valido en la linea " + lineCount + " se esperaba un * para abrir un comentario");
				while (c != -1) { // Estado 2 del AFD
					c = reader.read();
					if (c == '\n') {
						++lineCount;
					} else if (c == '*') { // Estado 3 del AFD: Lee hasta el ultimo * que encuentre seguido
						while (c == '*')
							c = reader.read();
						if(c == -1) {
							throw new LexerException("Error en la linea " + lineCount + ": Comentario no cerrado por falta de /");
						}
						else if (c == '/')
							break; // Paso del estado3 al 0:Termina de leer un comentario
					} // Paso del estado 3 al estado 2: Si no ha terminado de leer
				}
				if(c == -1) {
					throw new LexerException("Error en la linea " + lineCount + ": Comentario no cerrado");
				}
			} else if (c == '\n')
				++lineCount;
			else if (!Character.isWhitespace(c))
				return c;// Hemos llegado a un elemento no superfluo
			c = reader.read();
		}
		return c;
	}

	private Pair<Token, Object> words() throws IOException, LexerException, TSException { // Estado 4 del AFD
		String str = "";
		// Podemos poner el '_' de primeras porque ya hemos comprobado que el primero
		// sea una letra
		while (Character.isLetterOrDigit(c) || c == '_') {
			str += (char) c;
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
		case "int":
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
			return tsHandler.insert(str, lineCount);
		}
	}

	private Pair<Token, Object> cteEntera() throws IOException, LexerException { // Estado 6 del AFD
		int n = 0;
		while (Character.isDigit(c)) {
			n = n * 10 + (c - (int) '0');
			c = reader.read();
		}
		if (n > 32767) {
			throw new LexerException("Error en la linea " + lineCount + ": El numero entero " + n + " es mayor que el entero maximo, 32767");
		}
		return new Pair<Token, Object>(Token.CteENTERA, n);
	}

	private Pair<Token, Object> cteCadena() throws IOException, LexerException { // Estado 5 del AFD
		String str = "";
		c = reader.read();
		// Mientras no sea el final de la cadena y sea un caracter printable
		while (c != '"' && ' ' <= c && c <= '~') {
			if (c == '\\') {
				c = reader.read();
				if (c == 'n')
					str += '\n';
				else if (c == 't')
					str += '\t';
				else if (c == 'r')
					str += '\r';
				else if (c == 'b')
					str += '\b';
				else if (c == 'f')
					str += '\f';
				else if (c == '"')
					str += '"';
				else if (c == '\\')
					str += '\\';
				else if (c == '0')
					str += '\0';
				else if (c == '\'')
					str += '\'';
				else
					throw new LexerException(
							"Caracter no valido en la linea " + lineCount + ": La cadena " + str + " contiene una secuencia de escape no valida");
			}
			if (c == '\n') {
				throw new LexerException("Error en la linea " + lineCount + ": La cadena " + str + " no ha sido cerrada");
			}
			str += (char) c;
			c = reader.read();
		}
		if (c == -1) {
			throw new LexerException("Error en la linea " + lineCount + ": La cadena " + str + " no ha sido cerrada");
		} else if (c < ' ' || c > '~') {
			throw new LexerException("Error en la linea " + lineCount + ": La cadena " + str + " contiene un caracter no valido");
		} else if (str.length() > 64) {
			throw new LexerException("Error en la linea " + lineCount + ": La cadena " + str + "  ocupa mas de 64 bits");
		} else {
			c = reader.read();
		}
		return new Pair<Token, Object>(Token.CteCADENA, str);
	}

	public int getLineCount() {
		return lineCount;
	}

	public void toFile() {
		try (PrintWriter writer = new PrintWriter("tokens.txt")) {
			for (Pair<Token, Object> tokenPair : tokens) {
				switch (tokenPair.getKey()) {
					case CteENTERA:
						writer.println("< " + tokenPair.getKey().getCode() + ", " + ((Integer) tokenPair.getValue())+ " >");
						break;
					case CteCADENA:
						writer.println("< " + tokenPair.getKey().getCode() + ", \"" + ((String) tokenPair.getValue()) + "\" >");
						break;
					case ID:
						writer.println("< " + tokenPair.getKey().getCode() + ", " + ((Integer) tokenPair.getValue()) + " >");
						break;
					default:
						writer.println("< " + tokenPair.getKey().getCode() + ",  >");
						break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}