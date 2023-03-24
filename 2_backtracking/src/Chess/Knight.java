package Chess;

import java.awt.Point;
import java.util.ArrayList;

import utils.Config;
import utils.Helpers;

public class Knight extends Piece {

    private final Point[] permutations = {
            new Point(-1, -2),
            new Point(-2, -1),
            new Point(-1, 2),
            new Point(2, -1),
            new Point(1, -2),
            new Point(-2, 1),
            new Point(1, 2),
            new Point(2, 1)
    };

    public Knight() {
        super();
        this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_KNIGHT);
        this.symbol = 'K';
        this.name = Config.ASSET_NAME_OF_PIECE_KNIGHT;
        this.bgColor = Config.DEFAULT_BACKGROUND_COLOR_KNIGHT;
    }

    @Override
    public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
        ArrayList<Point> movements = new ArrayList<>();
        for (Point movement : permutations){
            Point temp = new Point(movement.x + piecePosition.x, movement.y + piecePosition.y);
            if (!boardState.sanityCheck(temp) || boardState.isOccupied(temp)){
                continue;
            }
            movements.add(temp);
        }
        return movements.toArray(Point[]::new);
    }
}
