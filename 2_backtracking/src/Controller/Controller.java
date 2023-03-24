package Controller;

import Chess.ChessBoard;
import Chess.Piece;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import utils.Config;

import java.awt.Color;
import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Controller implements Notify {

    private ChessBoard lastBoard;
    private Piece[][] boardWithMemory;
    private MVC hub;
    private int globalIteration;
    private int boardSize;
    private int elapsedTime;
    private boolean hasSolution;
    private boolean hasRestarted;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.globalIteration = 0;
        this.elapsedTime = 0;
        this.hasSolution = false;
        this.hasRestarted = false;
    }

    private boolean kingdomTour(boolean[][] visitedTowns, Deque<Point> pieces, ChessBoard board, int iteration) {

        if (this.hasRestarted) {
            return false;
        }

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

            this.boardWithMemory[movement.x][movement.y] = lastBoard.getPieceAt(movement);
            this.hub.notifyRequest(new Request(RequestCode.UPDATEMEMORYBOARD, this));

            this.hub.notifyRequest(new Request(RequestCode.UPDATEDBOARD, this));
            visitedTowns[movement.x][movement.y] = true;
            pieces.addLast(movement);

            if (kingdomTour(visitedTowns, pieces, board, globalIteration)) {
                return true;
            }
            if (this.hasRestarted) {
                return false;
            }

            board.move(movement, firstPiece);
            visitedTowns[movement.x][movement.y] = false;
            this.boardWithMemory[movement.x][movement.y] = null;
            this.hub.notifyRequest(new Request(RequestCode.UPDATEMEMORYBOARD, this));
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

    private boolean checkForPiece(Piece piece, Piece[] ps) {
        for (int i = 0; i < ps.length; i++) {
            if (ps[i] == null) {
                ps[i] = piece;
                return false;
            }
            if (ps[i].getName().equals(piece.getName())) {
                return true;
            }
        }
        return false;
    }

    private void run() {
        ChessBoard board = this.hub.getModel().getBoard();
        this.boardWithMemory = new Piece[board.height][board.width];
        Piece[] ps = new Piece[Config.NUM_OF_DIFFERENT_PIECES];
        for (Entry<Point, Piece> entry : board.getPieces()) {
            if (this.checkForPiece(entry.getValue(), ps)) {
                entry.getValue().setBgColor(new Color((int) (Math.random() * 0x1000000)));
            }
            this.boardWithMemory[entry.getKey().x][entry.getKey().y] = entry.getValue();
        }
        long start = System.nanoTime();
        this.hasSolution = this.kth(board);
        long end = System.nanoTime();
        this.elapsedTime = (int) Duration.ofNanos(end - start).toSeconds();
        this.hub.notifyRequest(new Request(RequestCode.HASFINISHED, this));
    }

    public int getElapsedTime() {
        return this.elapsedTime;
    }

    public boolean hasSolution() {
        return this.hasSolution;
    }

    public Piece[][] getBoardWithMemory() {
        return this.boardWithMemory;
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case START -> {
                this.hasRestarted = false;
                Thread.startVirtualThread(this::run);
            }
            case RESTART -> {
                this.hasRestarted = true;
                this.globalIteration = 0;
                this.elapsedTime = 0;
                this.hasSolution = false;
            }
            default -> {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
            }
        }
    }

    public int getIteration() {
        return this.globalIteration;
    }

    public ChessBoard getLastBoard() {
        return lastBoard;
    }
}
