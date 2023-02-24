package Model;

import Master.MVC;
import Request.Notify;
import Request.Request;

public class Model implements Notify {

    private MVC hub;

    public Model(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        this.hub.handleRequest(request);
    }

}
