package Model;

public class Piece {

    private int x;
    private int y;
    private int color;
    private String id;

    public Piece(int x, int y, int color, String id) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.id = id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getColor() {
        return this.color;
    }

    public String getId() {
        return this.id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}
