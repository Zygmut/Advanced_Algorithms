package Chess;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Queen extends ChessPiece {

    public Queen() {
        super();
        this.symbol = 'Q';
    }

    @Override
    public Point[] getMovements(Dimension boardDimension, Point position) {
        // Top
        Point[] topMovements = IntStream.range(0, position.y)
                .parallel()
                .mapToObj((y) -> new Point(position.x, y))
                .toArray(Point[]::new);

        // Top right
        Point[] topRightMovement = IntStream
                .range(1, Math.min(boardDimension.width - position.x + 1, position.y + 1))
                .mapToObj(i -> new Point(position.x + i, position.y - i))
                .toArray(Point[]::new);

        // Right
        Point[] rightMovements = IntStream.range(position.x + 1, boardDimension.width)
                .parallel()
                .mapToObj((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        // Bottom right
        Point[] bottomRightMovement = IntStream
                .range(1, Math.min(boardDimension.width - position.x, boardDimension.height - position.y))
                .mapToObj(i -> new Point(position.x + i, position.y + i))
                .toArray(Point[]::new);

        // Bottom
        Point[] bottomMovements = IntStream.range(position.y + 1, boardDimension.height)
                .parallel()
                .mapToObj(y -> new Point(position.x, y))
                .toArray(Point[]::new);

        // Bottom left
        Point[] bottomLeftMovement = IntStream.range(1, Math.min(position.x + 1, boardDimension.height - position.y + 1))
                .mapToObj(i -> new Point(position.x - i, position.y + i))
                .toArray(Point[]::new);

        // Left
        Point[] leftMovements = IntStream.range(0, position.x)
                .parallel()
                .mapToObj((x) -> new Point(x, position.y))
                .toArray(Point[]::new);

        // Top left
        Point[] topLeftMovement = IntStream.range(1, Math.min(position.x + 1, position.y + 1))
                .mapToObj(i -> new Point(position.x - i, position.y - i))
                .toArray(Point[]::new);

        return Stream
                .of(topMovements,
                        topRightMovement,
                        rightMovements,
                        bottomRightMovement,
                        bottomMovements,
                        bottomLeftMovement,
                        leftMovements,
                        topLeftMovement)
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
