package Model;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Point;
import java.time.Duration;
import java.util.Map.Entry;

import Chess.ChessBoard;
import Master.MVC;
import Request.Notify;
import Request.Request;
import utils.Config;

public class Model implements Notify {

    private MVC hub;
    private ChessBoard board;
    private Chess.Piece[][] boardWithMemory;
    private int iteration;
    private int elapsedTime;
    private boolean hasSolution;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 0;
        this.elapsedTime = 0;
        this.hasSolution = false;
        this.board = new ChessBoard(Config.INITIAL_DEFAULT_BOARD_SIZE);
        this.boardWithMemory = new Chess.Piece[this.board.height][this.board.width];
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UPDATEDBOARD -> {
                this.board = this.hub.getController().getLastBoard();
                this.iteration = this.hub.getController().getIteration();
            }
            case UPDATEMEMORYBOARD -> {
                this.boardWithMemory = this.hub.getController().getBoardWithMemory();
            }
            case CHANGEDTABLESIZE -> {
                this.board = new ChessBoard(this.hub.getView().getBoardWidth());
            }
            case CHANGEDPIECE -> {
                this.board.addPiece(this.hub.getView().getLastPiece(), this.hub.getView().getLastPoint());
            }
            case DELETEDPIECE -> {
                this.board.removePieceAt(this.hub.getView().getLastPoint());
            }
            case HASFINISHED -> {
                this.elapsedTime = this.hub.getController().getElapsedTime();
                this.hasSolution = this.hub.getController().hasSolution();
            }
            case RESTART -> {
                // Get all the pieces from the board
                var pieces = this.board.getPieces();
                this.board = new ChessBoard(this.hub.getView().getBoardWidth());
                for (Entry<Point, Chess.Piece> entry : pieces) {
                    this.board.addPiece(entry.getValue(), entry.getKey());
                }
                this.boardWithMemory = new Chess.Piece[this.board.height][this.board.width];
                this.iteration = 0;
                this.elapsedTime = 0;
                this.hasSolution = false;
            }
            default -> {
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
            }
        }
    }

    public Chess.Piece[][] getBoardWithMemory() {
        return boardWithMemory;
    }

    public boolean hasSolution() {
        return hasSolution;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getNumberOfPieces() {
        return this.board.getPieces().size();
    }

    public int getIteration() {
        return iteration;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

}
