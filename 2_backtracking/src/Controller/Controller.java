package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;

import Chess.Piece;
import Chess.Pieces;
import Chess.Board;
import Chess.Mark;

public class Controller implements Notify {

    private MVC hub;

    public Controller(MVC mvc) {
        this.hub = mvc;
    }

    private boolean kingdomTour(int[][] visitedTowns, Board kingdom, int iteration) {
        if (iteration == 50) {
            iteration = 50;
        }

        if (iteration == kingdom.size) {
            return true;
        }
        // Get the first element in the queue, removes it from it
        Entry<Point, Piece> piece = kingdom.getPieces().next();

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited
        Point[] movements = Arrays.stream(piece.getValue().getMovements(kingdom, piece.getKey()))
                .filter(move -> visitedTowns[move.x][move.y] == 0)
                .toArray(Point[]::new);
        //Arrays.stream(movements).map(elem -> "(" + elem.x + ", " + elem.y + ") ").forEach(System.out::print);
        //System.out.print("\033\143");

        for (Point movement : movements) {

            // add the piece with the new movement to the future kingdom queue
            kingdom.addPiece(piece.getValue(), movement);
            //System.out.println(kingdom.toString(visitedTowns));
            visitedTowns[movement.x][movement.y] = iteration + 1;

            // System.out.println("[DEBUG] "
            // + piece.getValue().getClass().getSimpleName()
            // + ": "
            // + "(" + movement.x + ", " + movement.y + ")");

            // recursivelly call
            if (kingdomTour(visitedTowns, kingdom, iteration + 1)) {
                return true;
            }

            visitedTowns[movement.x][movement.y] = 0;
        }

        return false;
    }

    private boolean kth(Board board) {
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
        Board board = this.hub.getModel().getBoard();
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
                this.run();
                // Thread.startVirtualThread(this::run);
                break;
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
    }

}
