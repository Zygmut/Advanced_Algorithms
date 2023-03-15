package Master;

import Chess.ChessBoard;
import Chess.Tower;
import mesurament.Mesurament;
import java.awt.Point;

public class Main {

    public static void main(String[] args) throws Exception {
        //Mesurament.mesura();
        //new MVC("config.txt").show();
        ChessBoard board = new ChessBoard(5,4);
        board.addPiece(new Tower(), new Point(4,1));
        System.out.println(board.toString());
    }

}
