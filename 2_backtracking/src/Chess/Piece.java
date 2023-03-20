package Chess;

import java.awt.Point;

public abstract class Piece implements ChessMovable {
    protected String imagePath;
    protected Character symbol;
    protected Point position;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String image_path) {
        this.imagePath = image_path;
    }

    public Character getSymbol() {
        return symbol;
    }

    public void setSymbol(Character symbol) {
        this.symbol = symbol;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

}