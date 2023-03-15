package Chess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class ChessPiece implements ChessMovement{
    protected BufferedImage image;
    protected Character symbol;

    public ChessPiece() {
        this.image = null;
    }

    public ChessPiece(BufferedImage image) {
        this.image = image;
    }

    public ChessPiece(File imageFile) throws IOException {
        this.image = ImageIO.read(imageFile);
    }

    public ChessPiece(String imagePath) throws IOException {
        this.image = ImageIO.read(new File(imagePath));
    }

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

}
