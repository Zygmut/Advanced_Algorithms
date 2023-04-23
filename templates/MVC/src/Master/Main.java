package Master;

import mesurament.Mesurament;

public class Main {

	public static void main(String[] args) {
		Mesurament.mesura();
		MVC mvc = new MVC("./config.json");
		mvc.show();
	}
}
