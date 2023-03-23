package Model;

import java.awt.Point;

import Chess.ChessBoard;
import Chess.Pieces;
import Master.MVC;
import Request.Notify;
import Request.Request;
import utils.Config;

public class Model implements Notify {

    private MVC hub;
    private ChessBoard board;
    private int iteration;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 0;
        this.board = new ChessBoard(Config.INITIAL_DEFAULT_BOARD_SIZE);
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard:
                this.board = this.hub.getController().getLastBoard();
                this.iteration = this.hub.getController().getIteration();
                break;
            case ChangedTableSize:
                this.board = new ChessBoard(this.hub.getView().getBoardSize());
                break;
            case ChangedPiece:
                this.board.addPiece(this.hub.getView().getLastPiece(), this.hub.getView().getLastPoint());
                break;
            default:
                System.err.printf("[MODEL]: %s is not implemented.\n", request.toString());
        }
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
                System.err.printf("[MODEL]: %s is not implemented.\n", piece);
        }
    }
}
