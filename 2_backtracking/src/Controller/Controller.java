package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import Chess.Piece;
import Chess.ChessBoard;

import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class Controller implements Notify {

    private ChessBoard lastBoard;
    private MVC hub;

    public Controller(MVC mvc) {
        this.hub = mvc;
    }

    private boolean kingdomTour(int[][] visitedTowns, ChessBoard kingdom, int iteration) {

        if (iteration == kingdom.size) {
            return true;
        }

        // Get the first element in the queue, removes it from it
        Entry<Point, Piece> entry = kingdom.getPieces().peek();

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited
        Point[] movements = Arrays.stream(entry.getValue().getMovements(kingdom, entry.getKey()))
                .filter(move -> visitedTowns[move.x][move.y] == 0)
                .toArray(Point[]::new);

        // Arrays.stream(movements).map(elem -> "(" + elem.x + ", " + elem.y + ")
        // ").forEach(System.out::print);
        // System.out.println(iteration);
        // System.out.print("\033\143");
        for (Point movement : movements) {

            // add the piece with the new movement to the future kingdom queue
            visitedTowns[movement.x][movement.y] = iteration + 1;

            // System.out.println("[DEBUG] "
            // + piece.getValue().getClass().getSimpleName()
            // + ": "
            // + "(" + movement.x + ", " + movement.y + ")");

            // System.out.println(b.toString(visitedTowns));
            this.lastBoard = new ChessBoard(kingdom.getDimension(),
                    kingdom.getPieces().rest().add(movement, entry.getValue()));

            this.safeThreadSleep(15);
            this.hub.notifyRequest(new Request(RequestCode.UpdateBoard, this));

            // recursivelly call
            if (kingdomTour(visitedTowns, this.lastBoard, iteration + 1)) {
                return true;
            }

            visitedTowns[movement.x][movement.y] = 0;
        }

        return false;
    }

    private boolean kth(ChessBoard board) {
        int[][] visited = new int[board.getDimension().height][board.getDimension().width];
        for (int[] column : visited) {
            Arrays.fill(column, 0);
        }

        int iter = 1;
        for (Point pos : board.getPieces().keySet()) {
            visited[pos.x][pos.y] = iter++;
        }

        return kingdomTour(visited, board, board.getPieces().keySet().size());
    }

    private void run() {
        ChessBoard board = this.hub.getModel().getBoard();
        boolean solution = this.kth(board);
        if (!solution) {
            throw new NoSuchElementException("No solution found");
        }
        System.out.println("Solution found!");
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Start:
                // this.run();
                Thread.startVirtualThread(this::run);
                break;
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
    }

    public ChessBoard getLastBoard() {
        return lastBoard;
    }

    private void safeThreadSleep(long millis) {
        try {
            Thread.sleep(Duration.ofMillis(millis));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
