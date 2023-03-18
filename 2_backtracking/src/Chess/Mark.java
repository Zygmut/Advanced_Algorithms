package Chess;

import java.awt.Point;

public class Mark extends ChessPiece {

    public Mark(){
        super();
        this.symbol = 'X';
    }
    @Override
    public Point[] getMovements(ChessBoard board_state, Point piece_position) {
        return new Point[]{};
    }
}
