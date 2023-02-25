package Master;

import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        //Mesurament.mesura();
        MVC mvc = new MVC();

        mvc.getView().initConfig("config.txt");
        mvc.getView().start();
    }
}
