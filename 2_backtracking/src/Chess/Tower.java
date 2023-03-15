package Chess;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Tower extends ChessPiece {

    public Tower() {
        super();
        this.symbol = 'T';
    }

    @Override
    public Point[] getMovements(Dimension boardDimension, Point position) {
        // Top
        Point[] topMovements = IntStream.range(0, position.y)
                .parallel()
                .mapToObj((y) -> new Point(position.x, y))
                .toArray(Point[]::new);

        // Right
        Point[] rightMovements = IntStream.range(position.x + 1, boardDimension.width)
                .parallel()
                .mapToObj((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        // Bottom
        Point[] bottomMovements = IntStream.range(position.y + 1, boardDimension.height)
                .parallel()
                .mapToObj(y -> new Point(position.x, y))
                .toArray(Point[]::new);
        // Left
        Point[] leftMovements = IntStream.range(0, position.x)
                .parallel()
                .mapToObj((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        return Stream.of(topMovements, rightMovements, bottomMovements, leftMovements)
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
