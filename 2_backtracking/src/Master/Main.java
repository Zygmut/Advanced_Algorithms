package Master;

import Chess.*;
import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;
import java.awt.Point;
import java.util.Arrays;

import org.jfree.chart.needle.PointerNeedle;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");

        // board.addPiece(new Queen(), queenpos);
        // board.addPiece(new Tower(), new Point(1, 1));
        // Arrays.stream(board.getPieces().get(pos).getMovements(board, pos))
        // .forEach(position -> board.addPiece(new Mark(), position));
        System.out.println(mvc.getModel().getBoard());
        mvc.show();

        mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

}
