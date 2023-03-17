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
        ChessBoard board = new ChessBoard(2, 2);
        mvc.getModel().setBoard(board);

        board.addPiece(new Queen(), new Point(0, 0));
        board.addPiece(new Tower(), new Point(1, 1));
        System.out.println(board.toString());
        board.getPieces()
                .getMap()
                .entrySet()
                .stream()
                .forEach(piece -> System.out.println(
                        piece.getValue().getClass().getSimpleName()
                                + ": "
                                + board.getMovementStringAt(piece.getKey())));
        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
