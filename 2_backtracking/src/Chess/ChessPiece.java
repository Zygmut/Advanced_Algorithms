package Chess;

import java.awt.image.BufferedImage;
import java.awt.Point;

public abstract class ChessPiece implements ChessMovement{
    protected BufferedImage image;
    protected Character symbol;
    protected Point position;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
