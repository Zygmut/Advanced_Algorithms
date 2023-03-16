package Master;

import Chess.*;
import mesurament.Mesurament;
import java.awt.Point;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        // new MVC("config.txt").show();
        ChessBoard board = new ChessBoard(5, 5);
        board.addPiece(new Knight(), new Point(3, 1));
        board.addPiece(new Knight(), new Point(0, 1));
        board.addPiece(new Tower(), new Point(2, 1));
        board.addPiece(new Knight(), new Point(0, 4));
        board.addPiece(new Queen(), new Point(4, 2));
        System.out.println(board.toString());
        board.getPieces()
                .entrySet()
                .stream()
                .forEach(piece -> System.out.println(
                        piece.getValue().getClass().getSimpleName()
                                + ": "
                                + Arrays.toString(
                                        Arrays.stream(piece
                                                .getValue()
                                                .getMovements(board, piece.getKey()))
                                                .map(move -> "(" + move.x + ", " + move.y + ")")
                                                .toArray(String[]::new))));

    }

}
