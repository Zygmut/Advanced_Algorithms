package Master;

import Chess.*;
import mesurament.Mesurament;
import java.awt.Point;
import java.awt.Dimension;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        // new MVC("config.txt").show();
        Point pos = new Point(0, 4);
        Dimension boardSize = new Dimension(5, 5);
        ChessBoard board = new ChessBoard(boardSize);
        board.addPiece(new Queen(), pos);
        System.out.println(board.getPieces().get(pos) + " at " + pos);
        System.out.println(board.toString());

        Arrays.stream(board.getPieces().get(pos).getMovements(boardSize, pos))
                .forEach(point -> System.out.print("(" + point.x + ", " + point.y + ") "));
    }

}
