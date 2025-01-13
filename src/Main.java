import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		TSHandler tsHandler = new TSHandler();

		try {
			Parser parser = new Parser("CodigoFalloLexer.txt", tsHandler);
			parser.parse();
			tsHandler.toFile("tabla_simbolos.txt");
		} catch (FileNotFoundException e ) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LexerException e) {
			e.printStackTrace();
		} catch (TSException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
}
