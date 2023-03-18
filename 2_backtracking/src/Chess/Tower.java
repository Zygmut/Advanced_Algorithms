package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Tower extends ChessPiece {

    public Tower() {
        super();
        this.symbol = 'T';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        Set<Point> currentPieces = board_state.getPieces().keySet();

        // Top
        Point[] topMovements = IntStream.range(0, piece_position.y)
                .boxed()
                .sorted(Collections.reverseOrder())
                .parallel()
                .map((y) -> new Point(piece_position.x, y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Right
        Point[] rightMovements = IntStream.range(piece_position.x + 1, board_state.getDimension().width)
                .parallel()
                .mapToObj((x) -> new Point(x, piece_position.y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Bottom
        Point[] bottomMovements = IntStream.range(piece_position.y + 1, board_state.getDimension().height)
                .parallel()
                .mapToObj(y -> new Point(piece_position.x, y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);
        // Left
        Point[] leftMovements = IntStream.range(0, piece_position.x)
                .boxed()
                .sorted(Collections.reverseOrder())
                .parallel()
                .map((x) -> new Point(x, piece_position.y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        return Stream.of(topMovements, rightMovements, bottomMovements, leftMovements)
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
