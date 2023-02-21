package View;

import Master.MVC;
import Request.Notify;
import Request.Request;

public class View implements Notify {
    private MVC hub;

    public View(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }


}
