package Model;

import java.time.Duration;
import java.util.ArrayList;
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

public class Model implements Notify {

    private MVC hub;
    private int iteration;
    private int batchSize;
    private ArrayList<Duration> escalarTimes;
    private ArrayList<Duration> modeNTimes;
    private ArrayList<Duration> modeNlognTimes;
    private Random rng;
    private Integer[] data;

    public Model(MVC mvc) {
        this.hub = mvc;
        this.iteration = 100;
        this.batchSize = 50;
        this.escalarTimes = new ArrayList<>();
        this.modeNTimes = new ArrayList<>();
        this.modeNlognTimes = new ArrayList<>();
        this.rng = new Random();
    }

    private void resetIterations() {
        this.iteration = 1;
    }

    private void nextIteration() {
        this.iteration += 1;
    }

    private Integer[] generateData(int bottomBoundary, int highBoundary) {
        return rng.ints(bottomBoundary, highBoundary).limit(this.iteration).boxed().toArray(Integer[]::new);
    }

    private Integer[] generateData() {
        return rng.ints(1, 100).limit(this.iteration).boxed().toArray(Integer[]::new);
    }

    private <T extends Number> Optional<double[]> declarativeEscalarProduct(T[] vec1, T[] vec2) {
        if (vec1.length != vec2.length) {
            return null;
        }
        // System.out.println("Escalar: " + Arrays.toString(vec1));

        return Optional.of(
                Arrays.stream(vec1)
                        .mapToDouble(Number::doubleValue)
                        .map(value1 -> Arrays.stream(vec2)
                                .mapToDouble(Number::doubleValue)
                                .reduce(0, (acumulator, value2) -> acumulator + value1 * value2))
                        .toArray());
    }

    private <T extends Number> long modeN(T[] data) {
        // System.out.println("ModeN: " + Arrays.toString(data));
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
        // System.out.println("ModeNLogN: " + Arrays.toString(data));
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

    private void calculateFor(RequestCode request) {
        this.data = this.generateData();
        switch (request) {
            case All_methods:
                this.escalarTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.declarativeEscalarProduct(data, data);
                        }, this.batchSize).sum(Duration::toNanos)));

                this.modeNTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeN(data);
                        }, this.batchSize).sum(Duration::toNanos)));

                this.modeNlognTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeNLogN(data);
                        }, this.batchSize).sum(Duration::toNanos)));
                break;
            case Escalar_Product:
                this.escalarTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.declarativeEscalarProduct(data, data);
                        }, this.batchSize).sum(Duration::toNanos)));
                break;
            case Mode_O_n:
                this.modeNTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeN(data);
                        }, this.batchSize).sum(Duration::toNanos)));
                break;
            case Mode_O_nlogn:
                this.modeNlognTimes.add(Duration.ofNanos(
                        TimeProfiler.batchTimeIt(() -> {
                            this.modeNLogN(data);
                        }, this.batchSize).sum(Duration::toNanos)));
                break;
            default:
                return;

        }

        this.nextIteration();
        this.hub.notifyRequest(new Request(RequestCode.New_data, this));
    }

    @Override
    public void notifyRequest(Request request) {
        switch (request.code) {
            case Set_batchSize:
                this.batchSize = 1; // TODO: Get Value from the component
                break;
            case All_methods:
            case Escalar_Product:
            case Mode_O_n:
            case Mode_O_nlogn:
                // Seguramente habrá que cambiar esto, pero no se como iría con lo threads.
                // this.resetIterations();

                this.calculateFor(request.code);

                break;
            case Stop_method:
                System.out.println("Model: TODO STOP");
                break;
            default:
                this.hub.notifyRequest(new Request(RequestCode.Error, this));
                return;
        }

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
        long[] data1 = new long[this.escalarTimes.size()];
        long[] data2 = new long[this.modeNTimes.size()];
        long[] data3 = new long[this.modeNlognTimes.size()];

        if (this.escalarTimes.size() == 0) {
            data1 = new long[] { 0 };
        } else {
            for (int i = 0; i < this.escalarTimes.size(); i++) {
                data1[i] = this.escalarTimes.get(i).toMillis();
            }
        }

        if (this.modeNTimes.size() == 0) {
            data2 = new long[] { 0 };
        } else {
            for (int i = 0; i < this.modeNTimes.size(); i++) {
                data2[i] = this.modeNTimes.get(i).toMillis();
            }
        }

        if (this.modeNlognTimes.size() == 0) {
            data3 = new long[] { 0 };
        } else {
            for (int i = 0; i < this.modeNlognTimes.size(); i++) {
                data3[i] = this.modeNlognTimes.get(i).toMillis();
            }
        }

        return new long[][] { data1, data2, data3 };

        // long[] a = { {
        // this.escalarTimes.stream().mapToLong(Duration::toMillis).toArray() }, {}, {}
        // };
    }
}
