package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

public class Tower extends Piece {

    public Tower() {
        super();
        this.symbol = 'T';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Stream.of(Movements.straightTop(piece_position, board_state),
                Movements.straightRight(piece_position, board_state),
                Movements.straightBottom(piece_position, board_state),
                Movements.straightLeft(piece_position, board_state))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }
}
