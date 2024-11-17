import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class ParserTable {
	private Hashtable<Integer, Hashtable<Token, Action>> accion;
	private Hashtable<Integer, Hashtable<Token, Integer>> goTo;

	public ParserTable() {
		accion = new Hashtable<Integer, Hashtable<Token, Action>>();
		goTo = new Hashtable<Integer, Hashtable<Token, Integer>>();

		for (int i = 0; i <= 105; i++) {
			accion.put(i, new Hashtable<Token, Action>());
			goTo.put(i, new Hashtable<Token, Integer>());
		}
		fillTable();
	}

	public Action getAction(int state, Token token) {
		return accion.get(state).get(token);
	}

	public int getGoTo(int state, Token token) {
		return goTo.get(state).get(token);
	}

	private void fillTable() {
		Token[] tokens = Token.values();

		try (BufferedReader br = new BufferedReader(new FileReader("tabla_parser.csv"))) {
            String line;
			line = br.readLine();
            while ((line = br.readLine()) != null) {
				String[] actions = line.split(",", -1);
				int state = Integer.parseInt(actions[0]);
				for (int i = 1; i < actions.length; i++) {
					if (actions[i].equals(""))
						continue;
					if (actions[i].equals("Aceptar"))
						accion.get(state).put(tokens[i - 1], Action.ACEPTAR);
					else if (actions[i].startsWith("d")) {
						accion.get(state).put(tokens[i - 1], Action.DESPLAZAR);
						goTo.get(state).put(tokens[i - 1], Integer.parseInt(actions[i].substring(1)));
					} else if (actions[i].startsWith("r"))
						accion.get(state).put(tokens[i - 1], Action.valueOf("REDUCIR_" + actions[i].substring(1)));
					else if (actions[i].matches("\\d+"))
						goTo.get(state).put(tokens[i - 1], Integer.parseInt(actions[i]));
					else
						throw new RuntimeException("Invalid action: " + actions[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}