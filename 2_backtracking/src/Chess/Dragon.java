package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

//https://chesscraft.ca/design?id=8RF
public class Dragon extends Piece {

    public Dragon() {
        super();
        this.symbol = 'D';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Stream
                .of(Movements.jumpPermutation(piece_position, board_state, new int[] { -1, 1 }, new int[] { -2, 2 }),
                        Movements.diagonalTopRight(piece_position, board_state),
                        Movements.diagonalBottomRight(piece_position, board_state),
                        Movements.diagonalBottomLeft(piece_position, board_state),
                        Movements.diagonalTopLeft(piece_position, board_state))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }
}
