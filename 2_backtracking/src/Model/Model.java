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

    public Model(MVC mvc) {
        this.hub = mvc;
        this.board = new ChessBoard(8);
        this.board.addPiece(Pieces.KNIGHT, new Point(0, 0));
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard:
                this.board = this.hub.getController().getLastBoard();
                break;
            default:
                throw new UnsupportedOperationException(
                        request + " is not implemented in " + this.getClass().getSimpleName());
        }
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

}
