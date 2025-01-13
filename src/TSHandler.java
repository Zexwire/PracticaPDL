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

	public void insertVariable(Integer pos, Atribute type) throws TSException {
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		Hashtable<Integer, ArrayList<Object>> localTS = activeTS.getValue();
		ArrayList<Object> atributes;

		if (localTS != null) {
			atributes = localTS.get(pos);
			if (atributes.size() > 1)
				throw new TSException("Error: El identificador '" + atributes.get(0) + "' ya tiene atributos");
			atributes.add(type);
			atributes.add(displacement.getValue());
			switch (type) {
				case ENT:
					displacement.setValue(displacement.getValue() + 2);//16 bits por entero
					break;
				case CAD:
					displacement.setValue(displacement.getValue() + 128);//128 bytes por cadena
					break;
				case LOG:
					displacement.setValue(displacement.getValue() + 2);//16 bits por logico
					break;
				default:
					throw new TSException("Error: Tipo de atributo no reconocido");
			}
		} else {
			atributes = globalTS.get(pos);
			if (atributes.size() > 1)
				throw new TSException("Error: El identificador '" + atributes.get(0) + "' ya tiene atributos");
			atributes.add(type);
			atributes.add(displacement.getKey());
			switch (type) {
				case ENT:
					displacement.setKey(displacement.getKey() + 2);//16 bits por entero
					break;
				case CAD:
					displacement.setKey(displacement.getKey() + 128);//128 bytes por cadena
					break;
				case LOG:
					displacement.setKey(displacement.getKey() + 2);//16 bits por logico
					break;
				default:
					throw new TSException("Error: Tipo de atributo no reconocido");
			}
		}
	}

	public void insertFunction(Integer pos, ArrayList<Object> parameters) throws TSException {
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		Hashtable<Integer, ArrayList<Object>> localTS = activeTS.getValue();
		ArrayList<Object> atributes = globalTS.get(pos);
		int i = 0;
		
		atributes.add(Atribute.FUN);
		// Hay que quitar 2 elementos del contador, el EMPTY del final y el tipo de retorno
		atributes.add((parameters.size() - 2) / 2);
		while (parameters.get(i) != Atribute.EMPTY) {
			atributes.add(parameters.get(i));
			insertVariable((Integer) parameters.get(i + 1), (Atribute) parameters.get(i));
			i += 2;
		}
		atributes.add(parameters.get(parameters.size() - 1));
		atributes.add("Et" + atributes.get(0) + pos);
	}

	public Atribute getAtribute(Integer pos) {
		ArrayList<Object> atributes = (activeTS.getValue() != null) ? activeTS.getValue().get(pos) : activeTS.getKey().get(pos);
		
		if (atributes == null)
			atributes = activeTS.getKey().get(pos);
		return (Atribute) atributes.get(1);
	}
	public String getLex(Integer pos) {
		return (String) ((activeTS.getValue() != null) ? activeTS.getValue().get(pos).get(0) : activeTS.getKey().get(pos).get(0));
	}

	public Atribute getReturnType(Integer integer, ArrayList<Object> aux) {
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		ArrayList<Object> atributes = globalTS.get(integer);
		if ((Integer) atributes.get(2) != ((Integer) aux.get(0)) - 1)
			return null;
		for (int i = 1; i < (Integer) atributes.get(2); i++) {
			if ((Atribute) atributes.get(i + 2) != (Atribute) aux.get(i))
				return null;
		}
		return (Atribute) atributes.get(atributes.size() - 2);
	}

	public void toFile(String fileName) throws TSException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			for (int i = 0; i < tsList.size(); i++) {
				writer.println("CONTENIDOS DE LA TABLA #" + i + ":\n");
				Hashtable<Integer, ArrayList<Object>> table = tsList.get(i);
				for (Entry<Integer, ArrayList<Object>> entry : table.entrySet()) {
					ArrayList<Object> atributes = entry.getValue();
					writer.println("\t* LEXEMA : '" + (String) atributes.get(0) + "'");
					if (!atributes.get(1).getClass().equals(Atribute.class))
						throw new TSException(
							"Atributo invalido para el identificador " + ((String) atributes.get(0)) + ": " + atributes.get(1));
					switch ((Atribute) atributes.get(1)) {
						case ENT:
							writer.println("\t\t+ tipo : 'entero'");
							writer.println("\t\t+ despl : " + (Integer) atributes.get(2));
							break;
						case CAD:
							writer.println("\t\t+ tipo : 'cadena'");
							writer.println("\t\t+ despl : " + (Integer) atributes.get(2));
							break;
						case LOG:
							writer.println("\t\t+ tipo : 'logico'");
							writer.println("\t\t+ despl : " + (Integer) atributes.get(2));
							break;
						case FUN:
							writer.println("\t\t+ tipo : 'funcion'");
							writer.println("\t\t+ numParam : " + (Integer) atributes.get(2));
							for (int j = 3; j < atributes.size() - 2; j++)
								writer.println("\t\t\t+ tipoParam" + (j - 2) + " : '" + ((Atribute) atributes.get(j)).toString() + "'");
							writer.println("\t\t+ tipoRetorno : '" + ((Atribute) atributes.get(atributes.size() - 2)).toString() + "'");
							writer.println("\t\t+ etiqFuncion : '" + (String) atributes.get(atributes.size() - 1) + "'");
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
