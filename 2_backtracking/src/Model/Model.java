package Model;

import java.awt.Point;

import Chess.ChessBoard;
import Chess.Pieces;
import Master.MVC;
import Request.Notify;
import Request.Request;

public class Model implements Notify {

    private MVC hub;
    private ChessBoard board;
    private int iteration;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 0;
        this.board = new ChessBoard(8);
        this.board.addPiece(Pieces.KNIGHT, new Point(0, 0));
        this.board.addPiece(Pieces.BISHOP, new Point(0, 1));
        this.board.addPiece(Pieces.CASTLE, new Point(0, 2));
        this.board.addPiece(Pieces.QUEEN, new Point(0, 3));
        this.board.addPiece(Pieces.KING, new Point(0, 4));
        this.board.addPiece(Pieces.DRAGON, new Point(0, 5));
        this.board.addPiece(Pieces.UNICORN, new Point(1, 3));
        this.board.addPiece(Pieces.TOWER, new Point(1, 4));
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard:
                this.board = this.hub.getController().getLastBoard();
                this.iteration = this.hub.getController().getIteration();
                break;
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
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
