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
		this.name = Config.ASSET_NAME_OF_PIECE_QUEEN;
		this.bgColor = Config.DEFAULT_BACKGROUND_COLOR_QUEEN;
	}

	@Override
	public Point[] getMovements(ChessBoard boardState, Point piecePosition) {
		return Stream.of(Movements.straightTop(piecePosition, boardState),
				Movements.diagonalTopRight(piecePosition, boardState),
				Movements.straightRight(piecePosition, boardState),
				Movements.diagonalBottomRight(piecePosition, boardState),
				Movements.straightBottom(piecePosition, boardState),
				Movements.diagonalBottomLeft(piecePosition, boardState),
				Movements.straightLeft(piecePosition, boardState),
				Movements.diagonalTopLeft(piecePosition, boardState))
				.flatMap(Arrays::stream)
				.toArray(Point[]::new);
	}

}
