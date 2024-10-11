import java.util.Hashtable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//TODO: implementación de arboles

public class TSHandler {
	//FIXME: Integer por ahora será su "posición" en la tabla, que después se cambiará por el arbol,
	//donde este valor será el valor de root
	private ArrayList<Hashtable<String, Integer>> tsList;
	private int currentTS;

	public TSHandler() {
		tsList = new ArrayList<Hashtable<String, Integer>>();
		tsList.add(new Hashtable<String, Integer>());
		currentTS = 0;
	}

	public void openScope() {
		tsList.add(new Hashtable<String, Integer>());
		currentTS++;
	}

	public void closeScope() {
		//FIXME: como hacer para que se puedan poner en el fichero aun cerrandolas
		tsList.remove(currentTS);
		currentTS--;
	}

	public void insert(String id) throws TSException {
		//TODO: comprobar que jamas va a entrar un null en esta función
		//Siempre estaremos añadiendo en la tabla de simbolos actual
		if (tsList.get(currentTS).containsKey(id))
			//FIXME: como conseguir que me pasen la linea en la que ha pasado el error
			throw new TSException("Variable " + id + " ya declarada");
		tsList.get(currentTS).put(id, null);
	}

	public void toFile (String fileName) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			for (int i = 0; i < tsList.size(); i++) {
				//FIXME: ver si el formato es válido y comprobar que se imprime correctamente
				writer.println("CONTENIDOS DE LA TABLA #" + i + ":\n");
				Hashtable<String, Integer> table = tsList.get(i);
				for (String key : table.keySet()) {
					writer.println(" * LEXEMA : '" + key + "'\n");
					//TODO: una vez implementado el arbol, printear el arbol
				}
				writer.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
