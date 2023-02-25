package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;

public class Controller implements Notify {

    private MVC hub;

    public Controller(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.notifyRequest(request);
    }

}
