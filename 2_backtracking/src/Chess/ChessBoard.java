package Chess;

import java.awt.Point;
import java.awt.Dimension;
import java.util.HashMap;

public class ChessBoard {
    private Dimension dimension;
    private HashMap<Point, ChessPiece> pieces;

    public ChessBoard() {
        this.dimension = new Dimension(8, 8);
        this.pieces = new HashMap<>();
    }

    public ChessBoard(Dimension dimension) {
        this.dimension = dimension;
        this.pieces = new HashMap<>();
    }

    public ChessBoard(int width, int height) {
        this.dimension = new Dimension(width, height);
        this.pieces = new HashMap<>();
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

    public HashMap<Point, ChessPiece> addPiece(ChessPiece piece, Point position) {
        if(position.x >= this.dimension.width || position.y >= this.dimension.height){
            throw new IllegalArgumentException("Position is out of the chess board");
        }
        this.pieces.put(position, piece);
        return this.pieces;
    }

    public HashMap<Point, ChessPiece> removePieceAt(Point position) {
        this.pieces.remove(position);
        return this.pieces;
    }

    public HashMap<Point, ChessPiece> getPieces() {
        return pieces;
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
            sb.append(y)
                    .append(" | ");
            for (int x = 0; x < dimension.width; x++) {
                Point position = new Point(x, y);
                ChessPiece piece = pieces.get(position);
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
}
