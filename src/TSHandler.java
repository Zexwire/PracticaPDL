import java.util.Hashtable;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//TODO: implementación de arboles

public class TSHandler {
	//FIXME: Cambiar string por tree
	private Pair<Hashtable<Integer, String>,Hashtable<Integer, String>> activeTS;
	private ArrayList<Hashtable<Integer, String>> tsList;
	private int currentTS;
	private boolean declarationZone;

	public TSHandler() {
		Hashtable<Integer, String> globalTS = new Hashtable<Integer, String>();

		activeTS = new Pair<Hashtable<Integer, String>,Hashtable<Integer, String>>(globalTS, null);
		tsList = new ArrayList<Hashtable<Integer, String>>();
		tsList.add(globalTS);
		currentTS = 0;
		declarationZone = false;
	}

	public void openScope() {
		Hashtable<Integer, String> localTS = new Hashtable<Integer, String>();

		activeTS.setValue(localTS);
		tsList.add(localTS);
		currentTS++;
	}

	public void closeScope() {
		activeTS.setValue(null);
		currentTS--;
	}

	public void setDeclarationZone(boolean declarationZone) {
		this.declarationZone = declarationZone;
	}

	public Pair<Token, Object> insert(String id, int line) throws TSException {
		//TODO:
		return null;
	}

	public void toFile(String fileName) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			for (int i = 0; i < tsList.size(); i++) {
				writer.println("CONTENIDOS DE LA TABLA #" + i + ":\n");
				Hashtable<Integer, String> table = tsList.get(i);
				for (Entry<Integer, String> entry : table.entrySet()) {
					writer.println("\t* LEXEMA : '" + entry.getValue() + "'");
					// TODO: una vez implementado el arbol, printear el arbol, tendrá que ser con
					// BEP
				}
				writer.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
