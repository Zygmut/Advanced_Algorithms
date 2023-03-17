package Model;

import Chess.ChessBoard;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Model implements Notify {

    private MVC hub;
    private ChessBoard board;

    public Model(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
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
