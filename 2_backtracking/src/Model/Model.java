package Model;

import Chess.Board;
import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Model implements Notify {

    private MVC hub;
    private Board board;

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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
