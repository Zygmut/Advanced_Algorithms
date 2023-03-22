package Master;

import java.util.Arrays;
import java.util.Map.Entry;
import java.awt.Point;
import java.time.Duration;

import Chess.ChessBoard;
import Chess.Movements;
import Chess.Piece;
import Chess.Pieces;
import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");

        System.out.println(mvc.getModel().getBoard());
        //mvc.show();
        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }


}
