import java.util.Hashtable;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TSHandler {
	private Pair<Hashtable<Integer, ArrayList<Object>>,Hashtable<Integer, ArrayList<Object>>> activeTS;
	private Pair<Integer, Integer> displacement;
	private Pair<Integer, Integer> lastPosTS;
	private ArrayList<Hashtable<Integer, ArrayList<Object>>> tsList;
	private boolean declarationZone;

	public TSHandler() {
		Hashtable<Integer, ArrayList<Object>> globalTS = new Hashtable<Integer, ArrayList<Object>>();
		activeTS = new Pair<Hashtable<Integer, ArrayList<Object>>,Hashtable<Integer, ArrayList<Object>>>(globalTS, null);
		displacement = new Pair<Integer, Integer>(0, 0);
		lastPosTS = new Pair<Integer, Integer>(0, 0);
		tsList = new ArrayList<Hashtable<Integer, ArrayList<Object>>>();
		tsList.add(globalTS);
		declarationZone = false;
	}

	public void openScope() {
		Hashtable<Integer, ArrayList<Object>> localTS = new Hashtable<Integer, ArrayList<Object>>();
		activeTS.setValue(localTS);
		tsList.add(localTS);
	}

	public void closeScope() {
		activeTS.setValue(null);
		displacement.setValue(0);
		lastPosTS.setValue(0);
	}

	public void setDeclarationZone(boolean declarationZone) {
		this.declarationZone = declarationZone;
	}

	public Pair<Token, Object> insert(String id, int line) throws TSException{
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		Hashtable<Integer, ArrayList<Object>> localTS = activeTS.getValue();
		ArrayList<Object> atributes = new ArrayList<Object>();

		atributes.add(id);
		if (declarationZone) {
			if (localTS != null) {
				for (Entry<Integer, ArrayList<Object>> entry : localTS.entrySet()) {
					if (entry.getValue().get(0).equals(id))
						throw new TSException("Error en la linea " + line + " el identificador '" + id + "' ya ha sido declarado en la función");
				}
				localTS.put(lastPosTS.getValue(), atributes);
				lastPosTS.setValue(lastPosTS.getValue() + 1);
				return new Pair<Token, Object>(Token.ID, lastPosTS.getValue() - 1);
			}
			for (Entry<Integer, ArrayList<Object>> entry : globalTS.entrySet()) {
				if (entry.getValue().get(0).equals(id))
					throw new TSException("Error en la linea " + line + " el identificador '" + id + "' ya ha sido declarado globalmente");
			}
			globalTS.put(lastPosTS.getKey(), atributes);
			lastPosTS.setKey(lastPosTS.getKey() + 1);
			return new Pair<Token, Object>(Token.ID, lastPosTS.getKey() - 1);
		} else {
			if (localTS != null) {
				for (Entry<Integer, ArrayList<Object>> entry : localTS.entrySet()) {
					if (entry.getValue().get(0).equals(id))
						return new Pair<Token, Object>(Token.ID, entry.getKey());
				}
			}
			for (Entry<Integer, ArrayList<Object>> entry : globalTS.entrySet()) {
				if (entry.getValue().get(0).equals(id))
					return new Pair<Token, Object>(Token.ID, entry.getKey());
			}
			atributes.add(Atribute.ENT);
			atributes.add(displacement.getKey());
			displacement.setKey(displacement.getKey() + 2);//2 bytes por entero
			globalTS.put(lastPosTS.getKey(), atributes);
			lastPosTS.setKey(lastPosTS.getKey() + 1);
			return new Pair<Token, Object>(Token.ID, lastPosTS.getKey() - 1);
		}
	}

	//TODO: meter los atributos y demás
	public void insertAtributes(Integer pos, ArrayList<Object> atributes) {
		
	}

	public void toFile(String fileName) throws TSException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			for (int i = 0; i < tsList.size(); i++) {
				writer.println("CONTENIDOS DE LA TABLA #" + i + ":\n");
				Hashtable<Integer, ArrayList<Object>> table = tsList.get(i);
				for (Entry<Integer, ArrayList<Object>> entry : table.entrySet()) {
					ArrayList<Object> atributes = entry.getValue();
					writer.println("\t* LEXEMA : '" + (String) atributes.get(0) + "'");
					// TODO: separar según formato
					if (!atributes.get(1).getClass().equals(Atribute.class))
						throw new TSException(
							"Atributo invalido para el identificador " + ((String) atributes.get(0)) + ": " + atributes.get(1));
					switch ((Atribute) atributes.get(1)) {
						case ENT:
							writer.println("\t\t* tipo : 'entero'");
							writer.println("\t\t* desp : " + (Integer) atributes.get(2));
							break;
						case CAD:
							writer.println("\t\t* tipo : 'cadena'");
							writer.println("\t\t* desp : " + (Integer) atributes.get(2));
							break;
						case LOG:
							writer.println("\t\t* tipo : 'logico'");
							writer.println("\t\t* desp : " + (Integer) atributes.get(2));
							break;
						default:
							throw new TSException(" de atributo no reconocido: " + atributes.get(1));
					}
				}
				writer.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
