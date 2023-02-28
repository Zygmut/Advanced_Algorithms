package Model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.ToLongFunction;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;

public class Model implements Notify {

    private MVC hub;
    private int iteration;
    private int batchSize;
    private ArrayList<Duration> escalarTimes;
    private ArrayList<Duration> modeNTimes;
    private ArrayList<Duration> modeNlognTimes;
    private ToLongFunction<? super Duration> timeStep;
    private Integer[] data;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 5000;
        this.batchSize = 1;
        this.escalarTimes = new ArrayList<>();
        this.modeNTimes = new ArrayList<>();
        this.modeNlognTimes = new ArrayList<>();
        this.timeStep = Duration::toNanos;
    }

    private void resetIterations() {
        this.iteration = 1;
    }

    private void nextIteration() {
        this.iteration += 1000;
        System.out.println("iteration: " + this.iteration);
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Set_batchSize:
                this.batchSize = 1; // TODO: Get Value from the component
                break;
            case New_data:
                this.collectData();
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

    private void collectData() {
        Duration[] lastData = this.hub.getController().getLastData();
        this.escalarTimes.add(lastData[0]);
        this.modeNlognTimes.add(lastData[1]);
        this.modeNTimes.add(lastData[2]);
        this.nextIteration();
        this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
    }

    public int getIteration() {
        return iteration;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public ArrayList<Duration> getEscalarTimes() {
        return escalarTimes;
    }

    public ArrayList<Duration> getModeNTimes() {
        return modeNTimes;
    }

    public ArrayList<Duration> getModeNlognTimes() {
        return modeNlognTimes;
    }

    public long[][] getData() {
        long[] data1 = this.escalarTimes.stream().mapToLong(this.timeStep).toArray();
        long[] data2 = this.modeNlognTimes.stream().mapToLong(this.timeStep).toArray();
        long[] data3 = this.modeNTimes.stream().mapToLong(this.timeStep).toArray();

        if (this.escalarTimes.size() == 0) {
            data1 = new long[] { 0 };
        }
        if (this.modeNlognTimes.size() == 0) {
            data2 = new long[] { 0 };
        }
        if (this.modeNTimes.size() == 0) {
            data3 = new long[] { 0 };
        }
        return new long[][] { data1, data2, data3 };
    }
}
