import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer{
    BufferedReader reader;
    TSHandler tsHandler;

    public Lexer(String fileName, TSHandler tsHandler) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(fileName));
        this.tsHandler = tsHandler;
    }

	public Pair<Token, Object> scan() throws LexerException, IOException {
		int c = reader.read();
		while(c != -1 && Character.isWhitespace(c)){
			c = reader.read();
		}
		if(c == -1){
			return new Pair<Token, Object>(Token.EOF, null);
		}
		switch(c){
			case '*':
				return new Pair<Token, Object>(Token.MULT, null);
			//TODO: completar con los otros tokens
			default:
		}
		//FIXME: placeholder para evitar error de compilación
		return null;
	}
}