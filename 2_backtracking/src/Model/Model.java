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
        this.board.addPiece(Pieces.KING, new Point(0, 0));
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case UpdateBoard:
                this.board = this.hub.getController().getLastBoard();
                this.iteration = this.hub.getController().getIteration();
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
        switch (piece) {
            case "king":
                this.board.addPiece(Pieces.KING, position);
                break;
            case "queen":
                this.board.addPiece(Pieces.QUEEN, position);
                break;
            case "tower":
                this.board.addPiece(Pieces.TOWER, position);
                break;
            case "knight":
                this.board.addPiece(Pieces.KNIGHT, position);
                break;
            case "bishop":
                this.board.addPiece(Pieces.BISHOP, position);
                break;
            case "unicorn":
                this.board.addPiece(Pieces.UNICORN, position);
                break;
            case "dragon":
                this.board.addPiece(Pieces.DRAGON, position);
                break;
            case "rook":
                this.board.addPiece(Pieces.CASTLE, position);
                break;
            default:
                System.err.printf("[MODEL]: %s is not implemented.\n", piece);
        }
    }


}
