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
		//TODO: Fill the table
	}

	public Action getAction(int state, Token token) {
		return accion.get(state).get(token);
	}

	public int getGoTo(int state, Token token) {
		return goTo.get(state).get(token);
	}
}
	// Las reducciones devolveran un int que se pasará por parser
	// en el cual habrá un switch que se encargará de hacer la reducción
	// eliminando los estados pertinentes y añadiendo el nuevo estado y no terminal