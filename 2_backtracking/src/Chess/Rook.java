package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.Config;
import utils.Helpers;

public class Rook extends Piece {

    public Rook() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_ROOK);
        this.symbol = 'R';
        this.name = Config.ASSET_NAME_OF_PIECE_ROOK;
        this.bgColor = Config.DEFAULT_BACKGROUND_COLOR_ROOK;
    }

    @Override
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        return Stream.of(Movements.straightTop(piecePosition, boardState),
                Movements.straightRight(piecePosition, boardState),
                Movements.straightBottom(piecePosition, boardState),
                Movements.straightLeft(piecePosition, boardState))
                .flatMap(Arrays::stream)
                .toArray(Point[]::new);
    }
}
