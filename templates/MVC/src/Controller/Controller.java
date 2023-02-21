package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Controller implements Notify {

    private MVC hub;
    private Request baseRequest = new Request(RequestCode.None, this, hub.getModel());

    public Controller(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

}
