package Model;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Model implements Notify {

    private MVC hub;

    public Model(MVC mvc) {
        this.hub = mvc;
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

    public long[][] getData() {
        throw new RuntimeException("Not implemented");
    }

}
