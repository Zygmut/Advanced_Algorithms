package Controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import Master.MVC;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import TimeProfiler.TimeProfiler;

public class Controller implements Notify {

    private MVC hub;
    private Random rng;
    // escalar, modeNLogN, modeN
    private Duration[] lastData;
    private boolean stop;
    private RequestCode currentExecution;
    private ToLongFunction<? super Duration> timeStep;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.rng = new Random();
        this.lastData = new Duration[3];
        this.stop = false;
    }

    private <T extends Number> Optional<double[]> declarativeEscalarProduct(T[] vec1, T[] vec2) {
        if (vec1.length != vec2.length) {
            return null;
        }
        return Optional.of(
                Arrays.stream(vec1)
                        .mapToDouble(Number::doubleValue)
                        .map(value1 -> Arrays.stream(vec2)
                                .mapToDouble(Number::doubleValue)
                                .reduce(0, (acumulator, value2) -> acumulator + value1 * value2))
                        .toArray());
    }

    private <T extends Number> long modeN(T[] data) {
        return Arrays.stream(data)
                .collect(Collectors.toMap(key -> key, value -> 1, Integer::sum))
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .get()
                .longValue();
    }

    private <T extends Number> long modeNLogN(T[] data) {
        return Arrays.stream(data)
                .sorted()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .get()
                .longValue();
    }

    private Integer[] generateData(int bottomBoundary, int highBoundary) {
        return rng.ints(bottomBoundary, highBoundary)
                .limit(this.hub.getModel().getIterationStep())
                .boxed()
                .toArray(Integer[]::new);
    }

    private Integer[] generateData() {
        return rng.ints(1, 100)
                .limit(this.hub.getModel().getIterationStep())
                .boxed()
                .toArray(Integer[]::new);
    }

    private void calculateFor(RequestCode request) {
        Integer[] data = this.generateData();
        switch (request) {
            case Mode_O_n:
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeN(data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));
                break;
            case Mode_O_nlogn:
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeNLogN(data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));
                this.lastData[2] = Duration.ZERO;
                break;
            case Escalar_Product:
                this.lastData[0] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.declarativeEscalarProduct(data, data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = Duration.ZERO;
                break;
            case All_methods:
                this.lastData[2] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeN(data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));

                this.lastData[1] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeNLogN(data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));

                this.lastData[0] = Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.declarativeEscalarProduct(data, data);
                        }, this.hub.getModel().getBatchSize()).sum(Duration::toNanos));
                break;
            default:
                return;

        }

        if (!this.stop) {
            this.hub.notifyRequest(new Request(RequestCode.New_data, this));
        }
    }

    private void run() {
        this.stop = false;
        while (!this.stop) {
            this.calculateFor(this.currentExecution);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case All_methods:
            case Escalar_Product:
            case Mode_O_n:
            case Mode_O_nlogn:
                // Seguramente habrá que cambiar esto, pero no se como iría con lo threads.
                // this.resetIterations();
                this.currentExecution = request.code;
            case Resume_execution:
                this.stop = true;
                Thread.startVirtualThread(this::run);
                break;
            case Pause_execution:
            case Reset_data:
                this.stop = true;
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }
    }

    public Duration[] getLastData() {
        return lastData;
    }

}
