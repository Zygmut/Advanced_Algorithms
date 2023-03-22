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
    }

    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        ArrayList<Point> movements = new ArrayList<>();
        for (Point movement : permutations){
            Point temp = new Point(movement.x + piece_position.x, movement.y + piece_position.y);
            if (!board_state.sanityCheck(temp) || board_state.isOccupied(temp)){
                continue;
            }
            movements.add(temp);
        }
        return movements.toArray(Point[]::new);
    }
}
