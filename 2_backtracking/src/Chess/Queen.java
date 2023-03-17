package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Queen extends ChessPiece {

    public Queen() {
        super();
        this.symbol = 'Q';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        Set<Point> currentPieces = board_state.getPieces().getMap().keySet();
        // Top
        Point[] topMovements = IntStream.range(0, piece_position.y)
                .boxed()
                .sorted(Collections.reverseOrder())
                .parallel()
                .map((y) -> new Point(piece_position.x, y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Top right
        Point[] topRightMovement = IntStream
                .range(1, Math.min(board_state.getDimension().width - piece_position.x ,
                        piece_position.y ))
                .mapToObj(i -> new Point(piece_position.x + i, piece_position.y - i))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Right
        Point[] rightMovements = IntStream.range(piece_position.x + 1, board_state.getDimension().width)
                .parallel()
                .mapToObj((x) -> new Point(x, piece_position.y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Bottom right
        Point[] bottomRightMovement = IntStream
                .range(1, Math.min(board_state.getDimension().width - piece_position.x,
                        board_state.getDimension().height - piece_position.y))
                .mapToObj(i -> new Point(piece_position.x + i, piece_position.y + i))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Bottom
        Point[] bottomMovements = IntStream.range(piece_position.y + 1, board_state.getDimension().height)
                .parallel()
                .mapToObj(y -> new Point(piece_position.x, y))
                .takeWhile(point -> !currentPieces.contains(point))
                .toArray(Point[]::new);

        // Bottom left
        Point[] bottomLeftMovement = IntStream
                .range(1, Math.min(piece_position.x ,
                        board_state.getDimension().height - piece_position.y ))
                .mapToObj(i -> new Point(piece_position.x - i, piece_position.y + i))
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

        // Top left
        Point[] topLeftMovement = IntStream.range(1, Math.min(piece_position.x+1 , piece_position.y+1 ) )
                .mapToObj(i -> new Point(piece_position.x - i, piece_position.y - i))
                .takeWhile(point -> !currentPieces.contains(point))
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
