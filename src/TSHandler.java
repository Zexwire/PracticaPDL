import java.util.Hashtable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//TODO: implementación de arboles

public class TSHandler {
	//FIXME: Object será un placeholder para esta entrega para los valores de las variables
	private ArrayList<Hashtable<String, Object>> tsList;
	private int currentTS;

	public TSHandler() {
		tsList = new ArrayList<Hashtable<String, Object>>();
		tsList.add(new Hashtable<String, Object>());
		currentTS = 0;
	}

	public void openScope() {
		tsList.add(new Hashtable<String, Object>());
		currentTS++;
	}

	public void closeScope() {
		//FIXME: como hacer para que se puedan poner en el fichero aun cerrandolas
		tsList.remove(currentTS);
		currentTS--;
	}

	public void insert(String id) {
		if (id == null) 
			throw new RuntimeException("Variable name cannot be null");
		//Siempre estaremos añadiendo en la tabla de simbolos actual
		if (tsList.get(currentTS).containsKey(id))
			//FIXME: por ahora lo dejo con excepción podría ser devolver valor
			throw new RuntimeException("Variable " + id + " already declared");
			tsList.get(currentTS).put(id, null);
	}

	public void toFile (String fileName) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			for (int i = 0; i < tsList.size(); i++) {
				//FIXME: ver si el formato es válido y comprobar que se imprime correctamente
				writer.println("CONTENIDOS DE LA TABLA #" + i + ":");
				Hashtable<String, Object> table = tsList.get(i);
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
