package master;

import mesurament.Mesurament;
import utils.Config;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC(Config.PATH_TO_WINDOW_CONFIG_FILE);
        mvc.show();
    }
}
