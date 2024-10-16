import java.util.Hashtable;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//TODO: implementación de arboles

public class TSHandler {
	// FIXME: Integer por ahora será su "posición" en la tabla, que después se
	// cambiará por el arbol,
	// donde este valor será el valor de root
	private ArrayList<Hashtable<Integer, String>> tsList;
	private Integer lastPosTS;
	private int currentTS;

	public TSHandler() {
		tsList = new ArrayList<Hashtable<Integer, String>>();
		tsList.add(new Hashtable<Integer, String>());
		lastPosTS = 0;
		currentTS = 0;
	}

	public void openScope() {
		tsList.add(new Hashtable<Integer, String>());
		lastPosTS = 0;
		currentTS++;
	}

	public void closeScope() {
		// FIXME: como hacer para que se puedan poner en el fichero aun cerrandolas
		tsList.remove(currentTS);
		lastPosTS = tsList.get(tsList.size() - 1).size() - 1;
		currentTS--;
	}

	public Pair<Token, Object> insert(String id, int line) throws TSException {
		Hashtable<Integer, String> actualTS = tsList.get(currentTS);
		for (Entry<Integer,String> entry : actualTS.entrySet()) {
			if (entry.getValue().equals(id))
				return new Pair<Token, Object>(Token.ID, entry.getKey());
		}
		actualTS.put(lastPosTS, id);
		lastPosTS++;
		return new Pair<Token, Object>(Token.ID, lastPosTS - 1);
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
