package Master;

import mesurament.Mesurament;

public class Main {
	private static final boolean DEBUG = true;

	public static void main(String[] args) {
		if (!DEBUG)
			Mesurament.mesura();
		MVC mvc = new MVC("./config.json");
		mvc.getController().setSeed(27);
		mvc.show();
	}
}
