package Chess;

import java.awt.Point;

public class Knight extends Piece {

    public Knight() {
        super();
        this.imagePath = "./assets/knight.png";
        this.symbol = 'K';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Movements.jumpPermutation(piece_position, board_state,
                new int[] { -1, 1 },
                new int[] { -2, 2 });
    }
}
