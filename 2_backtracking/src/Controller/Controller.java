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
import Chess.ChessPiece;

import Chess.ChessBoard;

public class Controller implements Notify {

    private MVC hub;
    private boolean stop;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.stop = true;
    }

    private boolean kingdomTour(Set<Point> visitedTowns, ChessBoard kingdom) {
        if (visitedTowns.size() == kingdom.size) {
            return true;
        }

        // Get the first element in the queue, removes it from it
        Entry<Point, ChessPiece> piece = kingdom.getPieces().next();

        // get all the possible movements from that piece and filter to get only the
        // ones that has not been visited
        Point[] movements = Arrays.stream(piece.getValue().getMovements(kingdom, piece.getKey()))
                .filter(move -> !visitedTowns.contains(move))
                .toArray(Point[]::new);

        for (Point movement : movements) {
            // Create a copy of the kingdom to not change the current value
            ChessBoard futureKingdom = kingdom.clone();
            Set<Point> futureVisitedTowns = new HashSet<>();
            futureVisitedTowns.addAll(visitedTowns);
            futureVisitedTowns.add(movement);

            // add the piece with the new movement to the future kingdom queue
            futureKingdom.addPiece(piece.getValue(), movement);

            // recursivelly call
            if (kingdomTour(futureVisitedTowns, futureKingdom)){
                return true;
            }
        }

        // Grab the tourist piece with the given turn
        return false;
    }

    private void run() {
        this.stop = false;
        while (!this.stop) {
            this.kingdomTour(new HashSet<Point>(), this.hub.getModel().getBoard());
            if (this.stop) {
                return;
            }
            try {
                // Try to lower the rate of unwanted thread executions
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1);
                    if (this.stop) {
                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Start:
                // Init algorithm
                stop = false;
                this.run();
                // Thread.startVirtualThread(this::run);
            case Stop:
                stop = true;
                break;
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
    }

}
