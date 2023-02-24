package TimeProfiler;

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

    public Duration[] getData() {
        return data;
    }

    /**
     * Given a function that converts a Duration object to a long, returns the sum
     * of all the values
     * i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).sum(Duration::toMillis);
     * }
     * </pre>
     *
     * Would return the sum of all the values in milliseconds
     *
     * @param timeStep ToLongFunction
     * @return long
     * @see ToLongFunction
     * @see Duration
     */
    public long sum(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep).sum();
    }

    /**
     * Given a function that converts a Duration object to a long, returns the mean
     * value of all the values
     * i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).mean(Duration::toNanos);
     * }
     * </pre>
     *
     * Would return the mean value in nanoseconds
     *
     * @param timeStep
     * @return double
     * @see ToLongFunction
     * @see Duration
     */
    public double mean(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .average()
                .getAsDouble();
    }

    /**
     * Returns the amount of values of the current data
     *
     * @return int
     */
    public int length() {
        return data.length;
    }

    /**
     * Given a function that converts a Duration object to a long, returns the
     * minimum value. i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).min(Duration::toSeconds);
     * }
     * </pre>
     *
     * Would return the minimum duration of them all in seconds
     *
     * @param timeStep
     * @return long
     * @see ToLongFunction
     * @see Duration
     */
    public long min(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .min()
                .getAsLong();
    }

    /**
     * Given a function that converts a Duration object to a long, returns the
     * maximum value. i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).max(Duration::toMinutes);
     * }
     * </pre>
     *
     * Would return the maximum duration of them all in minutes
     *
     * @param timeStep
     * @return long
     * @see ToLongFunction
     * @see Duration
     */
    public long max(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .max()
                .getAsLong();
    }

    /**
     * Returns a copy of the data that has been sorted
     *
     * @return TimeResult
     * @see TimeResult
     */
    public TimeResult sort() {
        return new TimeResult(
                (Duration[]) Arrays.stream(this.data)
                        .sorted()
                        .toArray());
    }

    /**
     * Given a function that converts a Duration object to a long, returns the
     * mode. If no mode was found returns the max value. i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).mode(Duration::toMinutes);
     * }
     * </pre>
     *
     * Would return the maximum duration of them all in minutes
     *
     * @param timeStep
     * @return long
     * @see ToLongFunction
     * @see Duration
     */
    public long mode(ToLongFunction<? super Duration> timeStep) {
        return timeStep.applyAsLong(Arrays.stream(this.data)
                .collect(Collectors.groupingBy(Duration::getNano))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .map(Duration::ofNanos)
                .orElseThrow(NoSuchElementException::new));

    }

    /**
     * Given a function that converts a Duration object to a long, returns the
     * median. i.e:
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).median(Duration::toHours);
     * }
     * </pre>
     *
     * Would return the median duration of them all in hours
     *
     * @param timeStep
     * @return long
     * @see ToLongFunction
     * @see Duration
     */
    public long median(ToLongFunction<? super Duration> timeStep) {
        return timeStep.applyAsLong(Arrays.stream(this.data)
                .sorted()
                .skip((this.data.length - 1) / 2)
                .limit(2 - this.data.length % 2)
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(2));

    }

    /**
     * Given a function that converts a Duration object to a long, returns a String
     * with all the values parsed. i.e
     *
     * <pre>
     * {@code
     * TimeProfiler.batchTimeIt(this::fn, 10).toString(Duration::toDays);
     * }
     * </pre>
     *
     * Would return a String with all the data parsed to days
     *
     * @param timeStep ToLongFunction
     * @return String
     * @see ToLongFunction
     * @see Duration
     */
    public String toString(ToLongFunction<? super Duration> timeStep) {
        return Arrays.stream(this.data)
                .mapToLong(timeStep)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
