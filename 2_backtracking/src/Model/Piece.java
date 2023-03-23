package model;

public record Piece(int x, int y, int color, String id) {
    public Piece {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y must be positive");
        }
        if (color != 0 && color != 1) {
            throw new IllegalArgumentException("color must be 0 or 1");
        }
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
    }
}
