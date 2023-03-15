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
        Point pos = new Point(7,4);
        board.addPiece(new Tower(), pos);
        System.out.println(board.getPieces().get(pos) + " at " + pos);
        System.out.println(board.toString());

        Arrays.stream(board.getPieces().get(pos).getMovements(new Dimension(8, 8), pos))
                .forEach(point -> System.out.print("(" + point.x + ", " + point.y + ") "));
    }

}
