package Master;

import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");
        System.out.println(mvc.getModel().getBoard());
        mvc.show();
    }
}
