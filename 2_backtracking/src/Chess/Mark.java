package Chess;

import java.awt.Point;

public class Mark extends Piece {

    public Mark(){
        super();
        this.symbol = 'X';
    }

    @Override
    public Point[] getMovements(Board board_state, Point piece_position) {
        return new Point[]{};
    }
}
