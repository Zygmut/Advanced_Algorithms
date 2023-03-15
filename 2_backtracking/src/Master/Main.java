package Master;

import Chess.ChessBoard;
import Chess.Tower;
import mesurament.Mesurament;
import java.awt.Point;
import java.awt.Dimension;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        // new MVC("config.txt").show();
        ChessBoard board = new ChessBoard();
        board.addPiece(new Tower(), new Point(6, 6));
        System.out.println(board.getPieces().get(new Point(6, 6)) + " at " + new Point(6, 6));
        System.out.println(board.toString());

        Arrays.stream(board.getPieces().get(new Point(6, 6)).getMovements(new Dimension(8, 8), new Point(6, 6)))
                .forEach(point -> System.out.print("(" + point.x + ", " + point.y + ") "));
    }

}
