package Model;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Model implements Notify {

    private MVC hub;
    private Request baseRequest = new Request(RequestCode.None, this, hub.getView());

    public Model(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

}
