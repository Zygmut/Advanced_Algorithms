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
        Point[] topMovements = IntStream.rangeClosed(
                0, position.y != 1 ? position.y - 1 : 0)
                .boxed()
                .map((y) -> new Point(position.x, y))
                .toArray(Point[]::new);

        // Right
        Point[] rightMovements = IntStream.rangeClosed(
                position.x != boardDimension.width ? position.x + 1 : boardDimension.width - 1,
                boardDimension.width - 1)
                .boxed()
                .map((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        // Bottom
        Point[] bottomMovements = IntStream.rangeClosed(
                position.y != boardDimension.height ? position.y + 1 : boardDimension.height - 1,
                boardDimension.height - 1)
                .boxed()
                .map((y) -> new Point(position.x, y))
                .toArray(Point[]::new);

        // Left
        Point[] leftMovements = IntStream.rangeClosed(
                0, position.x != 1 ? position.x - 1 : 0)
                .boxed()
                .map((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        return Stream.of(topMovements, rightMovements, bottomMovements, leftMovements).flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }


}
