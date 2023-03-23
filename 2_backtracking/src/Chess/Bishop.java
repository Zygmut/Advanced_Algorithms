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
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        return Stream.of(Movements.diagonalTopRight(piecePosition, boardState),
                Movements.diagonalBottomRight(piecePosition, boardState),
                Movements.diagonalBottomLeft(piecePosition, boardState),
                Movements.diagonalTopLeft(piecePosition, boardState))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }

}
