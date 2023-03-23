package Controller;

import Chess.ChessBoard;
import Chess.Piece;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.NoSuchElementException;

public class Controller implements Notify {

    private ChessBoard lastBoard;
    private MVC hub;
    private int globalIteration;
    private int boardSize;
    private int elapsedTime;
    private boolean stop;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.globalIteration = 0;
        this.elapsedTime = 0;
        this.stop = false;
    }

    private boolean kingdomTour(boolean[][] visitedTowns, Deque<Point> pieces, ChessBoard board, int iteration) {

        if (iteration == this.boardSize) {
            return true;
        }

        Point firstPiece = pieces.removeFirst();
        for (Point movement : board.getPieceAt(firstPiece).getMovements(board, firstPiece)) {
            if (visitedTowns[movement.x][movement.y]) {
                continue;
            }

            this.globalIteration = iteration + 1;
            board.move(firstPiece, movement);
            this.lastBoard = board;
            this.hub.notifyRequest(new Request(RequestCode.UPDATEDBOARD, this));
            visitedTowns[movement.x][movement.y] = true;
            pieces.addLast(movement);
            safeThreadSleep(10);

            if (kingdomTour(visitedTowns, pieces, board, globalIteration)) {

                return true;
            }

            board.move(movement, firstPiece);
            visitedTowns[movement.x][movement.y] = false;
            pieces.removeLast();
        }

        pieces.offerFirst(firstPiece);
        return false;
    }

    private boolean kth(ChessBoard board) {
        boolean[][] visited = new boolean[board.height][board.width];
        for (boolean[] column : visited) {
            Arrays.fill(column, false);
        }

        Deque<Point> queue = new ArrayDeque<>();
        for (Entry<Point, Piece> entry : board.getPieces()) {
            visited[entry.getKey().x][entry.getKey().y] = true;
            queue.add(entry.getKey());
        }

        this.boardSize = board.size;
        return kingdomTour(visited, queue, board, queue.size());
    }

    private void run() {
        ChessBoard board = this.hub.getModel().getBoard();
        long start = System.nanoTime();
        boolean solution = this.kth(board);
        long end = System.nanoTime();
        this.elapsedTime = (int) Duration.ofNanos(end - start).toSeconds();
        this.hub.notifyRequest(new Request(RequestCode.HASFINISHED, this));
        if (!solution) {
            throw new NoSuchElementException("No solution found");
        }
    }

    public int getElapsedTime() {
        return this.elapsedTime;
    }

    @Override
    public void notifyRequest(Request request) {
        if (request.code != RequestCode.START) {
            Logger.getLogger(this.getClass().getSimpleName())
                    .log(Level.SEVERE, "{0} is not implemented.", request);
            return;
        }
        Thread.startVirtualThread(this::run);
        if (request.code == RequestCode.STOP) {
        this.stop = true;
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
            Thread.currentThread().interrupt();
        }
    }

}
