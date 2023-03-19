package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.Set;
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

    public int countTrue(boolean[][] visitedTowns) {
        int count = 0;
        for (boolean[] col : visitedTowns) {
            for (boolean value : col) {
                if (value) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean kingdomTour(boolean[][] visitedTowns, Board kingdom) {
        // Get the first element in the queue, removes it from it
        Entry<Point, Piece> piece = kingdom.getPieces().next();
        visitedTowns[piece.getKey().x][piece.getKey().y] = true;

        if (countTrue(visitedTowns) == kingdom.size) {
            return true;
        }

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited
        Point[] movements = Arrays.stream(piece.getValue().getMovements(kingdom, piece.getKey()))
                .filter(move -> !visitedTowns[move.x][move.y])
                .toArray(Point[]::new);

        for (Point movement : movements) {

            // add the piece with the new movement to the future kingdom queue
            kingdom.addPiece(piece.getValue(), movement);

            // System.out.println("[DEBUG] "
            // + piece.getValue().getClass().getSimpleName()
            // + ": "
            // + "(" + movement.x + ", " + movement.y + ")");

            System.out.println(kingdom);
            // recursivelly call
            if (kingdomTour(visitedTowns, kingdom)) {
                return true;
            }

        }

        visitedTowns[piece.getKey().x][piece.getKey().y] = false;

        return false;
    }

    private void run() {
        Board board = this.hub.getModel().getBoard();
        boolean[][] visited = new boolean[board.getDimension().height][board.getDimension().width];
        for (boolean[] column : visited) {
            Arrays.fill(column, false);
        }
        boolean solution = this.kingdomTour(visited, board);
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
