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
    private int iterationStep;
    private int iterationStepAcumulator;
    private int iteration;
    private int batchSize;
    private Duration timeout;
    private ArrayList<Duration> escalarTimes;
    private ArrayList<Duration> modeNTimes;
    private ArrayList<Duration> modeNlognTimes;
    private ToLongFunction<? super Duration> timeStep;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iterationStep = 1000;
        this.iterationStepAcumulator = this.iterationStep;
        this.timeout = Duration.ofMillis(100);
        this.iteration = 0;
        this.batchSize = 1;
        this.escalarTimes = new ArrayList<>();
        this.modeNTimes = new ArrayList<>();
        this.modeNlognTimes = new ArrayList<>();
        this.timeStep = Duration::toNanos;
    }

    private void resetData(){
        this.escalarTimes.clear();
        this.modeNTimes.clear();
        this.modeNlognTimes.clear();
    }

    private void resetIterations() {
        this.iteration = 0;
        this.iterationStep = this.hub.getView().getIterationStep();
        this.iterationStepAcumulator = this.iterationStep;
    }

    private void nextIteration() {
        this.iterationStepAcumulator += this.iterationStep;
        this.iteration++;
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case New_data:
                this.collectData();
                break;
            case Reset_data:
                this.resetData();
                this.resetIterations();
                this.iterationStep = this.hub.getView().getIterationStep();
                this.batchSize = this.hub.getView().getBatchSize();
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Nanoseconds:
                this.timeStep = Duration::toNanos;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Milliseconds:
                this.timeStep = Duration::toMillis;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Seconds:
                this.timeStep = Duration::toSeconds;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Minutes:
                this.timeStep = Duration::toMinutes;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Hours:
                this.timeStep = Duration::toHours;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
                break;
            case Time_To_Days:
                this.timeStep = Duration::toDays;
                this.hub.notifyRequest(new Request(RequestCode.Show_data, this));
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

    public int getIterationStep() {
        return iterationStep;
    }

    public int getIterationStepAcumulator() {
        return iterationStepAcumulator;
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

    public Duration getTimeout(){
        return timeout;
    }

    public long getParsedTimeout(){
        return this.timeStep.applyAsLong(this.timeout);
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
