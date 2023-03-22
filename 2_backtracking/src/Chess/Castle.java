package Chess;

import java.awt.Point;

import utils.Config;
import utils.Helpers;

// https://chesscraft.ca/design?id=2ATC
public class Castle extends Piece {

    public Castle() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_CASTLE);
        this.symbol = 'C';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Movements.jumpPermutation(piece_position, board_state,
                new int[] { 2, -2 },
                new int[] { 0, 1, -1 });
    }

}
