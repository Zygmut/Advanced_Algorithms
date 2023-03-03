package Controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
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

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.rng = new Random();
        this.lastData = new Duration[] { Duration.ZERO, Duration.ZERO, Duration.ZERO };
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

    private Integer[] generateData(int bottomBoundary, int highBoundary, int limit) {
        return rng.ints(bottomBoundary, highBoundary)
                .limit(limit)
                .boxed()
                .toArray(Integer[]::new);
    }

    private boolean isGreater(Duration duration1, Duration duration2) {
        return duration1.compareTo(duration2) > 0 ? true : false;
    }

    private void calculateFor(RequestCode request) {
        Integer[] data = this.generateData(1, 100, this.hub.getModel().getIterationStepAcumulator());
        Duration timeout = this.hub.getModel().getTimeout();
        switch (request) {
            case Mode_O_n:
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = isGreater(lastData[2], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos(
                                (long) TimeProfiler.batchTimeIt(() -> {
                                    this.modeN(data);
                                }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                break;
            case Mode_O_nlogn:
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = isGreater(lastData[1], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos(
                                (long) TimeProfiler.batchTimeIt(() -> {
                                    this.modeNLogN(data);
                                }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                this.lastData[2] = Duration.ZERO;
                break;
            case Escalar_Product:
                this.lastData[0] = isGreater(lastData[0], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos(
                                (long) TimeProfiler.batchTimeIt(() -> {
                                    this.declarativeEscalarProduct(data, data);
                                }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = Duration.ZERO;
                break;
            case All_methods:
                this.lastData[2] = isGreater(lastData[2], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                            this.modeN(data);
                        }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));

                this.lastData[1] = isGreater(lastData[1], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                            this.modeNLogN(data);
                        }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));

                this.lastData[0] = isGreater(lastData[0], timeout) ? timeout.plus(Duration.ofNanos(1))
                        : Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                            this.declarativeEscalarProduct(data, data);
                        }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
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
                this.stop = true;
                break;
            case Reset_data:
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = Duration.ZERO;
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
