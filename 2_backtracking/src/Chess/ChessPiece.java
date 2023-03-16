package Chess;

import java.awt.image.BufferedImage;

public abstract class ChessPiece implements ChessMovement{
    protected BufferedImage image;
    protected Character symbol;

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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
