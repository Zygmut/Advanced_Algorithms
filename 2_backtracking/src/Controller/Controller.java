package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import java.awt.Point;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;

import Chess.Piece;

import Chess.Board;

public class Controller implements Notify {

    private MVC hub;

    public Controller(MVC mvc) {
        this.hub = mvc;
    }

    private Board kingdomTour(Set<Point> visitedTowns, Board kingdom) {
        if (visitedTowns.size() == kingdom.size) {
            return kingdom;
        }

        // Get the first element in the queue, removes it from it
        Entry<Point, Piece> piece = kingdom.getPieces().next();

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited
        Point[] movements = Arrays.stream(piece.getValue().getMovements(kingdom, piece.getKey()))
                .filter(move -> !visitedTowns.contains(move))
                .toArray(Point[]::new);

        for (Point movement : movements) {
            // Create a copy of the kingdom to not change the current value
            Board futureKingdom = kingdom.clone();
            Set<Point> futureVisitedTowns = visitedTowns;
            futureVisitedTowns.add(movement);

            // System.out.println("[DEBUG] "
            // + piece.getValue().getClass().getSimpleName()
            // + ": "
            // + "(" + movement.x + ", " + movement.y + ")");

            // add the piece with the new movement to the future kingdom queue
            futureKingdom.addPiece(piece.getValue(), movement);

            String str = futureKingdom.toString();

            // recursivelly call
            futureKingdom = kingdomTour(futureVisitedTowns, futureKingdom);

            if (futureKingdom != null) {
                System.out.println(str);
                return futureKingdom;
            }
        }

        return null;
    }

    private void run() {
        Board board = this.hub.getModel().getBoard();
        Set<Point> visited = new HashSet<>();
        visited.addAll(board.getPieces().keySet());
        board = this.kingdomTour(visited, board);
        if (board == null) {
            throw new NoSuchElementException("No solution found");
        }
        System.out.println("Solution found!");
        System.out.println(board);
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
