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
        Board board = new Board(5);
        mvc.getModel().setBoard(board);

        Point pos = new Point(2, 2);
        // board.addPiece(new Queen(), queenpos);
        // board.addPiece(new Tower(), new Point(1, 1));
        board.addPiece(Pieces.CASTLE, pos);

        Arrays.stream(board.getPieces().get(pos).getMovements(board, pos))
        .forEach(position -> board.addPiece(new Mark(), position));

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
