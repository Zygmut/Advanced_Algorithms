package Chess;

import java.awt.Color;
import java.awt.Point;

public abstract class Piece implements ChessMovable {
    protected String imagePath;
    protected Character symbol;
    protected Point position;
    protected Color bgColor;
    protected String name;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public Color getBgColor() {
        return this.bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
