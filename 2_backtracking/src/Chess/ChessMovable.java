package Chess;

import java.awt.Point;

public interface ChessMovable {
    /**
     * Given a board an the piece position, retuns an array of Point
     * with all the possible positions this piece could go
     *
     * @param boardState a chess board
     * @param piecePosition the current piece position
     * @return an array of points being all the possible positions this piece can go
     * @see Point
     * @see ChessBoard
     */
    public Point[] getMovements(ChessBoard boardState, Point piecePosition);
}
