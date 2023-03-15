package Chess;

import java.awt.Point;
import java.awt.Dimension;

public interface ChessMovement {
    /**
     * Given the board dimension an the piece position, retuns an array of Point
     * with all the possible positions this piece could go
     *
     * @param boardDimension The dimensions of the board
     * @param piece_position the current piece position
     * @return an array of points being all the possible positions this piece can go
     * @see Point
     * @see Dimension
     */
    public Point[] getMovements(Dimension boardDimension, Point piece_position);
}
