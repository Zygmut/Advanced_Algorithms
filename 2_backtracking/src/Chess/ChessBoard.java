package Chess;

import java.awt.Point;
import java.awt.Dimension;
import java.util.Arrays;

public class ChessBoard implements Cloneable {
	private Dimension dimension;
	private LinkedHashQueue<Point, Piece> pieces;
	public final int size;

	public ChessBoard() {
		this.dimension = new Dimension(8, 8);
		this.size = dimension.width * dimension.height;
		this.pieces = new LinkedHashQueue<>();
	}

	public ChessBoard(Dimension dimension) {
		this.dimension = dimension;
		this.size = dimension.width * dimension.height;
		this.pieces = new LinkedHashQueue<>();
	}

	public ChessBoard(Dimension dimension, LinkedHashQueue<Point, Piece> pieces) {
		this.dimension = dimension;
		this.size = dimension.width * dimension.height;
		this.pieces = pieces;
	}

	public ChessBoard(int width, int height) {
		this.dimension = new Dimension(width, height);
		this.size = dimension.width * dimension.height;
		this.pieces = new LinkedHashQueue<>();
	}

	public ChessBoard(int n) {
		this.dimension = new Dimension(n, n);
		this.size = n * n;
		this.pieces = new LinkedHashQueue<>();
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public void setDimension(int width, int height) {
		this.dimension = new Dimension(width, height);
	}

	public boolean sanityCheck(Point position) {
		return 0 <= position.x
				&& 0 <= position.y
				&& position.x < this.dimension.width
				&& position.y < this.dimension.height;
	}

	public boolean isOccupied(Point p) {
		return this.pieces.containsKey(p);
	}

	public LinkedHashQueue<Point, Piece> addPiece(Piece piece, Point position) {
		if (!sanityCheck(position)) {
			throw new IllegalArgumentException("Position is out of the chess board");
		}

		if (this.pieces.get(position) != null) {
			throw new IllegalArgumentException(
					"A piece already resides in position (" + position.x + ", " + position.y + ")");
		}
		this.pieces.put(position, piece);
		return this.pieces;
	}

	public LinkedHashQueue<Point, Piece> removePieceAt(Point position) {
		this.pieces.remove(position);
		return this.pieces;
	}

	public LinkedHashQueue<Point, Piece> getPieces() {
		return pieces;
	}

	public String getMovementStringAt(Point position) {
		return Arrays.toString(Arrays.stream(this.pieces
				.get(position)
				.getMovements(this, position))
				.map(move -> "(" + move.x + ", " + move.y + ")")
				.toArray(String[]::new));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chess board with dimension ")
				.append(dimension.width)
				.append("x")
				.append(dimension.height)
				.append(":\n");
		sb.append("  +");
		for (int x = 0; x < dimension.width; x++) {
			sb.append("---+");
		}
		sb.append("\n");

		for (int y = 0; y < dimension.height; y++) {
			sb.append(y).append(" | ");
			for (int x = 0; x < dimension.width; x++) {
				Point position = new Point(x, y);
				Piece piece = pieces.get(position);
				if (piece == null) {
					sb.append(" ");
				} else {
					sb.append(piece.getSymbol());
				}
				sb.append(" | ");
			}
			sb.append("\n  +");
			for (int x = 0; x < dimension.width; x++) {
				sb.append("---+");
			}
			sb.append("\n");
		}

		sb.append("    ");
		for (int x = 0; x < dimension.width; x++) {
			sb.append(x);
			sb.append("   ");
		}
		sb.append("\n");
		return sb.toString();
	}

	public String toString(boolean[][] visited) {
		StringBuilder sb = new StringBuilder();
		sb.append("Chess board with dimension ")
				.append(dimension.width)
				.append("x")
				.append(dimension.height)
				.append(":\n");
		sb.append("  +");
		for (int x = 0; x < dimension.width; x++) {
			sb.append("----+");
		}
		sb.append("\n");

		for (int y = 0; y < dimension.height; y++) {
			sb.append(y).append(" | ");
			for (int x = 0; x < dimension.width; x++) {
				Point position = new Point(x, y);
				Piece piece = pieces.get(position);
				if (visited[x][y]) {
					sb.append("x ");
				} else {
					if (piece == null) {
						sb.append("  ");
					} else {
						sb.append(piece.getSymbol()).append(" ");
					}
				}

				sb.append(" | ");
			}
			sb.append("\n  +");
			for (int x = 0; x < dimension.width; x++) {
				sb.append("----+");
			}
			sb.append("\n");
		}

		sb.append("    ");
		for (int x = 0; x < dimension.width; x++) {
			sb.append(x);
			sb.append("    ");
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public ChessBoard clone() {
		ChessBoard copy = null;
		try {
			copy = (ChessBoard) super.clone();
			copy.dimension = new Dimension(this.dimension.width, this.dimension.height);
			copy.pieces = new LinkedHashQueue<>();
			copy.pieces.putAll(this.pieces);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return copy;
	}

	public void setPieces(LinkedHashQueue<Point, Piece> pieces) {
		this.pieces = pieces;
	}
}
