package Controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
    private boolean isEscalarOverTimeOut, isModeNLogNOverTimeOut, isModeNOverTimeOut;
    private boolean isInitEscalar, isInitModeNLogN, isInitModeN;

    public Controller(MVC mvc) {
        this.hub = mvc;
        this.rng = new Random();
        this.lastData = new Duration[] { Duration.ZERO, Duration.ZERO, Duration.ZERO };
        this.stop = false;
        this.isEscalarOverTimeOut = false;
        this.isModeNLogNOverTimeOut = false;
        this.isModeNOverTimeOut = false;
        this.isInitEscalar = false;
        this.isInitModeNLogN = false;
        this.isInitModeN = false;
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

    private <T extends Number> long declarativeModeN(T[] data) {
        return Arrays.stream(data)
                .collect(Collectors.toMap(key -> key, value -> 1, Integer::sum))
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .get()
                .longValue();
    }

    private <T extends Number> long declarativeModeNLogN(T[] data) {
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

    @SuppressWarnings("unused")
    private <T extends Number> long imperativeModeNLogN(T[] data) {
        Arrays.sort(data);

        int maxFrequency = 0, currentFrequency = 0;
        long currentMode = data[0].longValue(), prev = currentMode;

        for (T d : data) {
            long val = d.longValue();
            if (val == prev) {
                currentFrequency++;
            } else {
                if (currentFrequency > maxFrequency) {
                    maxFrequency = currentFrequency;
                    currentMode = prev;
                }
                prev = val;
                currentFrequency = 1;
            }
        }
        return currentFrequency > maxFrequency ? prev : currentMode;
    }

    @SuppressWarnings("unused")
    private <T extends Number> long imperativeModeN(T[] data) {
        Map<Long, Integer> frequencyMap = new HashMap<>();

        for (T d : data) {
            long val = d.longValue();
            frequencyMap.put(val, frequencyMap.getOrDefault(val, 0) + 1);
        }

        long currentMode = data[0].longValue();
        int maxFrequency = 0;

        for (Map.Entry<Long, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                currentMode = entry.getKey();
            }
        }

        return currentMode;
    }

    @SuppressWarnings("unused")
    private <T extends Number> Optional<double[]> imperativeEscalarProduct(T[] vec1, T[] vec2) {
        if (vec1.length != vec2.length) {
            return null;
        }

        double[] result = new double[vec1.length];
        Arrays.fill(result, 0);

        for (int i = 0; i < vec1.length; i++) {
            double parsedValue1 = vec1[i].doubleValue();
            for (int j = 0; j < vec2.length; j++) {
                result[0] += parsedValue1 * vec2[j].doubleValue();
            }
        }

        return Optional.of(result);
    }

    private Integer[] generateData(int bottomBoundary, int highBoundary, int limit) {
        return rng.ints(bottomBoundary, highBoundary)
                .limit(limit)
                .boxed()
                .toArray(Integer[]::new);
    }

    private void calculateFor(RequestCode request) {
        if (isModeNLogNOverTimeOut && isModeNOverTimeOut && isEscalarOverTimeOut) {
            this.stop = true;
        }

        Integer[] data = this.generateData(1, 100, this.hub.getModel().getIterationStepAcumulator());
        Duration timeout = this.hub.getModel().getTimeout();
        switch (request) {
            case Mode_O_n:
                this.isEscalarOverTimeOut = true;
                this.isModeNLogNOverTimeOut = true;
                this.isInitModeNLogN = true;
                this.isInitEscalar = true;
                this.isInitModeN = false;
                if (this.lastData[2].compareTo(timeout) < 0) {
                    this.lastData[2] = Duration.ofNanos(
                            (long) TimeProfiler.batchTimeIt(() -> {
                                this.declarativeModeN(data);
                            }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isModeNOverTimeOut = true;
                }
                break;
            case Mode_O_nlogn:
                this.isEscalarOverTimeOut = true;
                this.isModeNOverTimeOut = true;
                isInitModeN = true;
                isInitEscalar = true;
                isInitModeNLogN = false;
                if (this.lastData[1].compareTo(timeout) < 0) {
                    this.lastData[1] = Duration.ofNanos(
                            (long) TimeProfiler.batchTimeIt(() -> {
                                this.declarativeModeNLogN(data);
                            }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isModeNLogNOverTimeOut = true;
                }
                break;
            case Escalar_Product:
                this.isModeNLogNOverTimeOut = true;
                this.isModeNOverTimeOut = true;
                isInitModeN = true;
                isInitModeNLogN = true;
                isInitEscalar = false;
                if (this.lastData[0].compareTo(timeout) < 0) {
                    this.lastData[0] = Duration.ofNanos(
                            (long) TimeProfiler.batchTimeIt(() -> {
                                this.declarativeEscalarProduct(data, data);
                            }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isEscalarOverTimeOut = true;
                }
                break;
            case All_methods:
                if (this.lastData[2].compareTo(timeout) < 0) {
                    this.lastData[2] = Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                        this.declarativeModeN(data);
                    }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isModeNOverTimeOut = true;
                }

                if (this.lastData[1].compareTo(timeout) < 0) {
                    this.lastData[1] = Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                        this.declarativeModeNLogN(data);
                    }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isModeNLogNOverTimeOut = true;
                }

                if (this.lastData[0].compareTo(timeout) < 0) {
                    this.lastData[0] = Duration.ofNanos((long) TimeProfiler.batchTimeIt(() -> {
                        this.declarativeEscalarProduct(data, data);
                    }, this.hub.getModel().getBatchSize()).mean(Duration::toNanos));
                } else {
                    this.isEscalarOverTimeOut = true;
                }
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
                this.currentExecution = request.code;
            case Resume_execution:
                this.stop = true;
                Thread.startVirtualThread(this::run);
                break;
            case Pause_execution:
                this.stop = true;
                break;
            case Reset_data:
                this.isEscalarOverTimeOut = false;
                this.isModeNLogNOverTimeOut = false;
                this.isModeNOverTimeOut = false;
                this.lastData[0] = Duration.ZERO;
                this.lastData[1] = Duration.ZERO;
                this.lastData[2] = Duration.ZERO;
                this.isInitEscalar = false;
                this.isInitModeN = false;
                this.isInitModeNLogN = false;
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

    public boolean isEscalarOverTimeOut() {
        return isEscalarOverTimeOut;
    }

    public boolean isModeNLogNOverTimeOut() {
        return isModeNLogNOverTimeOut;
    }

    public boolean isModeNOverTimeOut() {
        return isModeNOverTimeOut;
    }

    public boolean isInitEscalar() {
        return isInitEscalar;
    }

    public boolean isInitModeN() {
        return isInitModeN;
    }

    public boolean isInitModeNLogN() {
        return isInitModeNLogN;
    }

}
