package Master;

import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");

        //System.out.println(mvc.getModel().getBoard());
        mvc.show();

        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
