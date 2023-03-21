package Master;

import java.util.Arrays;
import java.util.Map.Entry;
import java.awt.Point;
import java.rmi.server.SocketSecurityException;
import java.time.Duration;

import Chess.ChessBoard;
import Chess.Movements;
import Chess.Piece;
import Chess.Pieces;
import Request.Request;
import Request.RequestCode;
import mesurament.Mesurament;

public class Main {

    public static void main(String[] args) throws Exception {
        // Mesurament.mesura();
        MVC mvc = new MVC("config.txt");

        // System.out.println(mvc.getModel().getBoard());
        // mvc.show();
        long start = System.nanoTime();
        System.out.println(mvc.getModel().getBoard());
        if (!kth(mvc.getModel().getBoard())) {
            System.out.print("no ");
        }
        long end = System.nanoTime();
        System.out.println("solution found");
        System.out.println("Time elapsed: " + Duration.ofNanos(end- start).toSeconds());

        // mvc.notifyRequest(new Request(RequestCode.Start, "main"));

    }

    private static boolean kingdomTour(boolean[][] visitedTowns, Piece piece, Point pos, ChessBoard kingdom,
            int iteration) {
        // System.out.print("\033\143");
        // System.out.println("Iteration " + iteration + " wanting " + kingdom.size );
        if (iteration == kingdom.size) {
            return true;
        }

        // Get the first element in the queue, removes it from it
        // Entry<Point, Piece> entry = kingdom.getPieces().peek();

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited

        // Arrays.stream(movements).map(elem -> "(" + elem.x + ", " + elem.y + ")
        // ").forEach(System.out::print);
        // System.out.println(iteration);
        for (Point movement : piece.getMovements(kingdom, pos)) {
            if (visitedTowns[movement.x][movement.y]) {
                continue;
            }

            // add the piece with the new movement to the future kingdom queue
            visitedTowns[movement.x][movement.y] = true;

            // System.out.println("[DEBUG] "
            // + piece.getValue().getClass().getSimpleName()
            // + ": "
            // + "(" + movement.x + ", " + movement.y + ")");

            // System.out.println(kingdom.toString(visitedTowns));

            // recursivelly call
            if (kingdomTour(visitedTowns, Pieces.KNIGHT, movement, kingdom, iteration + 1)) {
                return true;
            }

            visitedTowns[movement.x][movement.y] = false;
        }

        return false;
    }

    private static boolean kth(ChessBoard board) {
        boolean[][] visited = new boolean[board.getDimension().height][board.getDimension().width];
        for (boolean[] column : visited) {
            Arrays.fill(column, false);
        }

        visited[0][0] = true;

        return kingdomTour(visited, Pieces.KNIGHT, new Point(0, 0), board, 1);
    }

}
