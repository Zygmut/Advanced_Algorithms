package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.Config;
import utils.Helpers;

// https://chesscraft.ca/design?id=2ATB
public class Unicorn extends Piece {

    public Unicorn() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_UNICORN);
        this.symbol = 'U';
    }

    @Override
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        return Stream.of(Movements.straightTop(piecePosition, boardState),
                Movements.diagonalBottomRight(piecePosition, boardState),
                Movements.diagonalBottomLeft(piecePosition, boardState))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
