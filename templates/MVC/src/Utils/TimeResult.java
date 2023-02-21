package Utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class TimeResult {
    private Duration[] data;

    public TimeResult(Duration[] data) {
        this.data = data;
    }

    public void setData(Duration[] data) {
        this.data = data;
    }

    public Duration[] getData() {
        return data;
    }

    public long sum(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep).sum();
    }

    public double mean(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .average()
                .getAsDouble();
    }

    public double length() {
        return data.length;
    }

    public long min(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .min()
                .getAsLong();
    }

    public long max(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .max()
                .getAsLong();
    }

    public TimeResult sort(ToLongFunction<? super Duration> timeStep) {
        return new TimeResult(
                (Duration[]) Arrays.stream(this.data)
                        .sorted()
                        .toArray());
    }

    public Duration mode() {
        return Arrays.stream(this.data)
                .collect(Collectors.groupingBy(Duration::getNano))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .map(Duration::ofNanos)
                .orElseThrow(NoSuchElementException::new);
    }

    public Duration median(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .sorted()
                .skip((this.data.length - 1) / 2)
                .limit(2 - this.data.length % 2)
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(2);
    }
}
