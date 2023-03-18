package Master;

import Chess.*;
import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;
import java.awt.Point;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");
        ChessBoard board = new ChessBoard(4, 3);
        mvc.getModel().setBoard(board);

        Point queenpos = new Point(3,2);
        board.addPiece(new Queen(), queenpos);
        board.addPiece(new Tower(), new Point(1, 0));
        Arrays.stream(board.getPieces().getMap().get(queenpos).getMovements(board, queenpos)).forEach(pos -> board.addPiece(new Mark(), pos));
        board.getPieces()
                .getMap()
                .entrySet()
                .stream()
                .forEach(piece -> System.out.println(
                        piece.getValue().getClass().getSimpleName()
                                + ": "
                                + board.getMovementStringAt(piece.getKey())));
        System.out.println(board.toString());


                                //mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
