package Chess;

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.Config;
import utils.Helpers;

public class Queen extends Piece {

	public Queen() {
		super();
		this.imagePath = Helpers.getAssetPath(Config.ASSET_NAME_OF_PIECE_QUEEN);
		this.symbol = 'Q';
	}

	@Override
	public Point[] getMovements(ChessBoard board_state, Point piece_position) {
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
