package Chess;

import java.awt.Point;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class ChessBoard {
	Piece[][] content;
	public final int size;
	public final int width;
	public final int height;

	public ChessBoard() {
		this.content = new Piece[8][8];
		this.width = 8;
		this.height = 8;
		this.size = 64;
	}

	public ChessBoard(Dimension dimension) {
		this.content = new Piece[dimension.width][dimension.height];
		this.width = dimension.width;
		this.height = dimension.height;
		this.size = dimension.width * dimension.height;
	}

	public ChessBoard(int width, int height) {
		this.content = new Piece[width][height];
		this.width = width;
		this.height = height;
		this.size = width * height;
	}

	public ChessBoard(int n) {
		this.content = new Piece[n][n];
		this.width = n;
		this.height = n;
		this.size = n * n;
	}

	public boolean sanityCheck(Point position) {
		return 0 <= position.x
				&& 0 <= position.y
				&& position.x < this.width
				&& position.y < this.height;
	}

	public boolean isOccupied(Point p) {
		return this.content[p.x][p.y] != null;
	}

	public Piece getPieceAt(Point p) {
		return this.content[p.x][p.y];
	}

	public void move(Point from, Point to) {
		this.content[to.x][to.y] = this.content[from.x][from.y];
		this.content[from.x][from.y] = null;
	}

	public Piece[][] addPiece(Piece piece, Point position) {
		if (!sanityCheck(position)) {
			throw new IllegalArgumentException("Position is out of the chess board");
		}

		if (isOccupied(position)) {
			throw new IllegalArgumentException(
					"A piece already resides in position (" + position.x + ", " + position.y + ")");
		}
		this.content[position.x][position.y] = piece;
		return this.content;
	}

	public Piece[][] removePieceAt(Point position) {
		this.content[position.x][position.y] = null;
		return this.content;
	}

	public ArrayList<Entry<Point, Piece>> getPieces() {
		ArrayList<Entry<Point, Piece>> pieces = new ArrayList<>();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (this.content[x][y] != null) {
					pieces.add(new SimpleEntry<Point, Piece>(new Point(x, y), content[x][y]));
				}
			}
		}
		return pieces;
	}

	public String getMovementStringAt(Point position) {
		return Arrays.toString(Arrays.stream(this.content[position.x][position.y]
				.getMovements(this, position))
				.map(move -> "(" + move.x + ", " + move.y + ")")
				.toArray(String[]::new));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chess board with dimension ")
				.append(width)
				.append("x")
				.append(height)
				.append(":\n");
		sb.append("  +");
		for (int x = 0; x < width; x++) {
			sb.append("---+");
		}
		sb.append("\n");

		for (int y = 0; y < height; y++) {
			sb.append(y).append(" | ");
			for (int x = 0; x < width; x++) {
				Piece piece = this.content[x][y];
				if (piece == null) {
					sb.append(" ");
				} else {
					sb.append(piece.getSymbol());
				}
				sb.append(" | ");
			}
			sb.append("\n  +");
			for (int x = 0; x < width; x++) {
				sb.append("---+");
			}
			sb.append("\n");
		}

		sb.append("    ");
		for (int x = 0; x < width; x++) {
			sb.append(x);
			sb.append("   ");
		}
		sb.append("\n");
		return sb.toString();
	}

	public String toString(boolean[][] visited) {
		StringBuilder sb = new StringBuilder();
		sb.append("Chess board with dimension ")
				.append(width)
				.append("x")
				.append(height)
				.append(":\n");
		sb.append("  +");
		for (int x = 0; x < width; x++) {
			sb.append("----+");
		}
		sb.append("\n");

		for (int y = 0; y < height; y++) {
			sb.append(y).append(" | ");
			for (int x = 0; x < width; x++) {
				Piece piece = this.content[x][y];
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
			for (int x = 0; x < width; x++) {
				sb.append("----+");
			}
			sb.append("\n");
		}

		sb.append("    ");
		for (int x = 0; x < width; x++) {
			sb.append(x);
			sb.append("    ");
		}
		sb.append("\n");
		return sb.toString();
	}
}
