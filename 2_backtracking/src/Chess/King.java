package Chess;

import java.awt.Point;

import utils.Config;
import utils.Helpers;

public class King extends Piece {

    public King() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_KING);
        this.symbol = 'K';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        // TODO: Comprobar si es correcto este movimiento
        return Movements.jumpPermutation(piece_position, board_state, new int[] { -1, 0, 1 }, new int[] { -1, 0, 1 });
    }
}
