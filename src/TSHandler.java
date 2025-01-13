import java.util.Hashtable;
import java.util.Map.Entry;

import enums.Atribute;
import enums.Token;
import exceptions.TSException;

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

	/**
	 * Función que inserta un identificador en la tabla de símbolos desde el Lexer según la zona de declaración
	 * 
	 * @param id Identificador a insertar
	 * @param line Línea en la que se encuentra el identificador
	 * @return Par con el token del identificador y su posición en la tabla de símbolos
	 * @throws TSException Excepción lanzada si el identificador ya ha sido declarado
	 */
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

	/**
	 * Función que inserta los atributos de una variable en la tabla de símbolos desde el Parser
	 * 
	 * @param pos Posición del identificador en la tabla de símbolos
	 * @param type Tipo de la variable a insertar
	 * @throws TSException Excepción lanzada si el identificador ya tiene atributos o si el tipo de atributo no es de variable
	 */
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

	/**
	 * Función que inserta los atributos de una función en la tabla de símbolos desde el Parser
	 * 
	 * @param pos Posición del identificador en la tabla de símbolos
	 * @param parameters Lista de parámetros de la función, con el tipo primero y la posición despúes, acabando con Atribute.EMPTY y el tipo de retorno de la función
	 * @throws TSException Excepción lanzada si los parámetros de la función están duplicados
	 */
	public void insertFunction(Integer pos, ArrayList<Object> parameters) throws TSException {
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		ArrayList<Object> atributes = globalTS.get(pos);
		int i = 0;
		
		atributes.add(Atribute.FUN);
		// Hay que quitar 2 elementos del contador, el EMPTY del final y el tipo de retorno
		atributes.add((parameters.size() - 2) / 2);
		while (parameters.get(i) != Atribute.EMPTY) {
			// Añadimos el tipo de paramétro a la lista de atributos de la función
			atributes.add(parameters.get(i));
			// Añadimos los parametros a la tabla de simbolos de la función
			insertVariable((Integer) parameters.get(i + 1), (Atribute) parameters.get(i));
			i += 2;
		}
		// Añadimos el tipo de retorno y la etiqueta a la lista de atributos de la función
		atributes.add(parameters.get(parameters.size() - 1));
		atributes.add("Et" + atributes.get(0) + pos);
	}

	/**
	 * Función que devuelve el tipo de un identificador en la tabla de símbolos
	 * 
	 * @param pos Posición del identificador en la tabla de símbolos
	 * @return Tipo del identificador
	 */
	public Atribute getType(Integer pos) {
		ArrayList<Object> atributes = (activeTS.getValue() != null) ? activeTS.getValue().get(pos) : activeTS.getKey().get(pos);
		
		// Si estamos en una función pero no se ha encontrado el identificador en la tabla local, lo buscamos en la global
		if (atributes == null)
			atributes = activeTS.getKey().get(pos);
		return (Atribute) atributes.get(1);
	}
	/**
	 * Función que devuelve el lexema de un identificador en la tabla de símbolos
	 * 
	 * @param pos Posición del identificador en la tabla de símbolos
	 * @return Lexema del identificador
	 */
	public String getLex(Integer pos) {
		ArrayList<Object> atributes = (activeTS.getValue() != null) ? activeTS.getValue().get(pos) : activeTS.getKey().get(pos);
		
		// Si estamos en una función pero no se ha encontrado el identificador en la tabla local, lo buscamos en la global
		if (atributes == null)
			atributes = activeTS.getKey().get(pos);
		return (String) atributes.get(0);
	}

	/**
	 * Función que devuelve el tipo de retorno de una función en la tabla de símbolos y comprueba si los parámetros de la llamada son correctos
	 * 
	 * @param pos Posición de la función en la tabla de símbolos
	 * @param aux Lista de parámetros de la llamada a la función
	 * @return Tipo de retorno de la función o null si los parámetros de la llamada no son correctos
	 */
	public Atribute getReturnType(Integer pos, ArrayList<Object> aux) {
		Hashtable<Integer, ArrayList<Object>> globalTS = activeTS.getKey();
		ArrayList<Object> atributes = globalTS.get(pos);
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
