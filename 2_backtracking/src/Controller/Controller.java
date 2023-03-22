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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Controller implements Notify {

    private ChessBoard lastBoard;
    private MVC hub;
    private int globalIteration;
    private int boardSize;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.globalIteration = 0;
    }

    private boolean kingdomTour(boolean[][] visitedTowns, Deque<Entry<Point, Piece>> pieces, int iteration) {

        if (iteration == this.boardSize) {
            return true;
        }

        for (Point movement : pieces.getFirst().getValue().getMovements(lastBoard, null)) {
            if(visitedTowns[movement.x][movement.y]){
                continue;
            }

            // add the piece with the new movement to the future kingdom queue
            visitedTowns[movement.x][movement.y] = iteration + 1;
            this.globalIteration = iteration + 1;

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
        int[][] visited = new int[board.height][board.width];
        for (int[] column : visited) {
            Arrays.fill(column, 0);
        }

        Deque<Entry<Point, Piece>> queue = new ArrayDeque<>();
        int iter = 1;
        for (Entry<Point,Piece> entry: board.getPieces()) {
            visited[entry.getKey().x][entry.getKey().y] = iter++;
            queue.add(entry);
        }

        this.boardSize = board.size;
        return kingdomTour(visited, queue, iter);
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
            case Stop:
                // TODO: stop the thread
            default:
                System.err.printf("[CONTROLLER]: %s is not implemented.\n", request.toString());
        }
    }

    public int getIteration() {
        return this.globalIteration;
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
