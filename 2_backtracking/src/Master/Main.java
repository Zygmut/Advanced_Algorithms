package Master;

import Chess.*;
import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;
import java.awt.Point;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");
        ChessBoard board = new ChessBoard(3, 3);
        mvc.getModel().setBoard(board);

        board.addPiece(new Knight(), new Point(0, 0));
        board.addPiece(new Tower(), new Point(2, 1));
        System.out.println(board.toString());
        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
