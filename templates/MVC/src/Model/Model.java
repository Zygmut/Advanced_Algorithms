package Model;

import Main.MVC;
import Request.Notify;
import Request.Request;

public class Model implements Notify {

    private MVC hub;
    private int x;
    private int y;

    public Model(MVC mvc) {
        this.hub = mvc;
        x = 0;
        y = 0;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

}
