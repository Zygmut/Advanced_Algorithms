package Chess;

import java.awt.Dimension;
import java.awt.Point;
import java.util.stream.Stream;

public class Tower extends ChessPiece {

    public Tower(){
        super();
        this.symbol = 'T';
    }

    @Override
    public Point[] getMovements(Dimension boardDimension, Point piece_position) {
        // Calculate the bounds given the position

        // Top
        Stream<Point> topMovements = null;
        // Right
        Stream<Point> rightMovements = null;
        // Bottom
        Stream<Point> bottomMovements = null;
        // Left
        Stream<Point> leftMovements = null;


        return Stream.of(topMovements, rightMovements, bottomMovements, leftMovements).toArray(Point[]::new);
    }
}
