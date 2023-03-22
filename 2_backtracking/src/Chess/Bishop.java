package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.Config;
import utils.Helpers;

public class Bishop extends Piece {

    public Bishop() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_BISHOP);
        this.symbol = 'B';
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return Stream.of(Movements.diagonalTopRight(piece_position, board_state),
                Movements.diagonalBottomRight(piece_position, board_state),
                Movements.diagonalBottomLeft(piece_position, board_state),
                Movements.diagonalTopLeft(piece_position, board_state))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
