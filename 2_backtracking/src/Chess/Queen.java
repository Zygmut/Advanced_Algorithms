package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

public class Queen extends Piece {

	public Queen() {
		super();
		this.symbol = 'Q';
	}

	@Override
	public Point[] getMovements(Board board_state, Point piece_position) {
		return Stream.of(Movements.straightTop(piece_position, board_state),
				Movements.diagonalTopRight(piece_position, board_state),
				Movements.straightRight(piece_position, board_state),
				Movements.diagonalBottomRight(piece_position, board_state),
				Movements.straightBottom(piece_position, board_state),
				Movements.diagonalBottomLeft(piece_position, board_state),
				Movements.straightLeft(piece_position, board_state),
				Movements.diagonalTopLeft(piece_position, board_state))
				.flatMap(Arrays::stream)
				.toArray(Point[]::new);
	}

}
