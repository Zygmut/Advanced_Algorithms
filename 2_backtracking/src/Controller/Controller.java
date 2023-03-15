package Controller;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import TimeProfiler.TimeProfiler;

public class Controller implements Notify {

    private MVC hub;
    private boolean stop;
    private RequestCode currentExecution;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.stop = true;
    }

    private void run() {
        this.stop = false;
        while (!this.stop) {
            // TODO: Add your code here
            if (this.stop) {
                return;
            }
            try {
                // Try to lower the rate of unwanted thread executions
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(1);
                    if (this.stop) {
                        return;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

}
