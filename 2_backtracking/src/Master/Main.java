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
        ChessBoard board = new ChessBoard(8);
        mvc.getModel().setBoard(board);

        // Point queenpos = new Point(0, 0);
        // board.addPiece(new Queen(), queenpos);
        // board.addPiece(new Tower(), new Point(1, 1));
        board.addPiece(new Tower(), new Point(0,0));
        // Arrays.stream(board.getPieces().get(queenpos).getMovements(board, queenpos))
        // .forEach(pos -> board.addPiece(new Mark(), pos));

        board.getPieces()
                .entrySet()
                .stream()
                .forEach(piece -> System.out.println(
                        piece.getValue().getClass().getSimpleName()
                                + ": "
                                + board.getMovementStringAt(piece.getKey())));
        System.out.println(board.toString());

        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
