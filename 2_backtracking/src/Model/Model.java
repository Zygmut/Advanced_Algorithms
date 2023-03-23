package model;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

import chess.ChessBoard;
import chess.Pieces;
import master.MVC;
import request.Notify;
import request.Request;
import utils.Config;

public class Model implements Notify {

    private MVC hub;
    private ChessBoard board;
    private int iteration;
    private int elapsedTime;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 0;
        this.elapsedTime = 0;
        this.board = new ChessBoard(Config.INITIAL_DEFAULT_BOARD_SIZE);
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UPDATEDBOARD:
                this.board = this.hub.getController().getLastBoard();
                this.iteration = this.hub.getController().getIteration();
                break;
            case CHANGEDTABLESIZE:
                this.board = new ChessBoard(this.hub.getView().getBoardWidth());
                break;
            case CHANGEDPIECE:
                this.board.addPiece(this.hub.getView().getLastPiece(), this.hub.getView().getLastPoint());
                break;
            case DELETEDPIECE:
                this.board.removePieceAt(this.hub.getView().getLastPoint());
                break;
            case HASFINISHED:
                this.elapsedTime = this.hub.getController().getElapsedTime();
                break;
            default:
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", request);
        }
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

    public void setNewPiece(String piece, Point position) {
        board = this.hub.getModel().getBoard();
        switch (piece) {
            case Config.ASSET_NAME_OF_PIECE_KING:
                this.board.addPiece(Pieces.KING, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_QUEEN:
                this.board.addPiece(Pieces.QUEEN, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_ROOK:
                this.board.addPiece(Pieces.ROOK, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_KNIGHT:
                this.board.addPiece(Pieces.KNIGHT, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_BISHOP:
                this.board.addPiece(Pieces.BISHOP, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_UNICORN:
                this.board.addPiece(Pieces.UNICORN, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_DRAGON:
                this.board.addPiece(Pieces.DRAGON, position);
                break;
            case Config.ASSET_NAME_OF_PIECE_CASTLE:
                this.board.addPiece(Pieces.CASTLE, position);
                break;
            default:
                Logger.getLogger(this.getClass().getSimpleName())
                        .log(Level.SEVERE, "{0} is not implemented.", piece);
        }
    }
}
