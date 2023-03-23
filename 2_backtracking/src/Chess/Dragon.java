package chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.Config;
import utils.Helpers;

//https://chesscraft.ca/design?id=8RF
public class Dragon extends Piece {

    public Dragon() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_DRAGON);
        this.symbol = 'D';
    }

    @Override
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        return Stream
                .of(Movements.jumpPermutation(piecePosition, boardState, new int[] { -1, 1 }, new int[] { -2, 2 }),
                        Movements.diagonalTopRight(piecePosition, boardState),
                        Movements.diagonalBottomRight(piecePosition, boardState),
                        Movements.diagonalBottomLeft(piecePosition, boardState),
                        Movements.diagonalTopLeft(piecePosition, boardState))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }
}
