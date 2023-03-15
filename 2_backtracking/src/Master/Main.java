package Master;

import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        Mesurament.mesura();
        new MVC("config.txt").show();
    }

}
