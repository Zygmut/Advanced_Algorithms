package Chess;

import java.awt.Point;

import utils.Config;
import utils.Helpers;

public class King extends Piece {

    public King() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_KING);
        this.symbol = 'K';
        this.name = Config.ASSET_NAME_OF_PIECE_KING;
        this.bgColor = Config.DEFAULT_BACKGROUND_COLOR_KING;
    }

    @Override
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        return Movements.jumpPermutation(piecePosition, boardState, new int[] { -1, 0, 1 }, new int[] { -1, 0, 1 });
    }
}
