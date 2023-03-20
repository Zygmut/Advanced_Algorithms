package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

// https://chesscraft.ca/design?id=2ATB
public class Unicorn extends Piece {

    public Unicorn() {
        super();
        this.symbol = 'U';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Stream.of(Movements.straightTop(piece_position, board_state),
                Movements.diagonalBottomRight(piece_position, board_state),
                Movements.diagonalBottomLeft(piece_position, board_state))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
