package Chess;

import java.awt.Point;
import java.util.Arrays;

public class Knight extends Piece {

    private final Point[] permutations = {
            new Point(-1, -2),
            new Point(-2, -1),
            new Point(-1, 2),
            new Point(2, -1),
            new Point(1, -2),
            new Point(-2, 1),
            new Point(1, 2),
            new Point(2, 1)
    };

    public Knight() {
        super();
        this.imagePath = "./assets/knight.png";
        this.symbol = 'K';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Arrays.stream(this.permutations)
                .map(move -> new Point(move.x + piece_position.x, move.y + piece_position.y))
                .filter(point -> board_state.sanityCheck(point) && !board_state.isOccupied(point))
                .toArray(Point[]::new);
    }
}
