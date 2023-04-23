package Master;

import mesurament.Mesurament;
import utils.Config;

public class Main {
	private static final boolean DEBUG = true;

	public static void main(String[] args) {
		if (!DEBUG)
			Mesurament.mesura();
		MVC mvc = new MVC(Config.CONFIG_PATH_TO_MAIN_WINDOW);
		mvc.getController().setSeed(27);
		mvc.show();
	}
}
