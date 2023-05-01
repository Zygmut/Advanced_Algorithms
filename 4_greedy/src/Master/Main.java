package Master;


import mesurament.Mesurament;
import utils.Config;

public class Main {

	public static void main(String[] args) {
		if (!Config.DEBUG) {
			Mesurament.mesura();
		}
		MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		mvc.start();
	}
}
