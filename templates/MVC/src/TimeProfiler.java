import java.time.Duration;
import java.util.Arrays;
import java.util.stream.LongStream;

public class TimeProfiler {

    /**
     * Given a runnable function returns a Duration object with the time that was
     * spent running that function. It doesn't contemplate errors within the
     * function.
     *
     * @param function
     * @return Duration
     */
    public static Duration timeIt(Runnable function) {
        long startingNanoSeconds = System.nanoTime();
        function.run();
        long endingNanoSeconds = System.nanoTime();
        return Duration.ofNanos((endingNanoSeconds - startingNanoSeconds));
    }

    /**
     * Given a runnable funtion returns a Duration object with the mean value of
     * time spent running that function. The amount of times that the function is
     * executed is defined by batchSize. If batchSize is <= 0, returns a Duration of
     * 0.
     *
     * @param function
     * @param batchSize Amount of times that the function will be executed
     * @return Duration
     */
    public static Duration batchTimeIt(Runnable function, int batchSize) {
        if (batchSize <= 0) {
            return Duration.ofNanos(0);
        }

        long startingNanoSeconds, endingNanoSeconds;
        long[] durations = new long[batchSize];
        for (int i = 0; i < batchSize; i++) {
            startingNanoSeconds = System.nanoTime();
            function.run();
            endingNanoSeconds = System.nanoTime();
            durations[i] = endingNanoSeconds - startingNanoSeconds;
        }
        return Duration.ofNanos(LongStream.of(durations).sum() / batchSize);
    }

    /**
     * Given a runnable array of functions returns a Duration array object with the
     * time that was
     * spent running each one of those functions. It doesn't contemplate errors
     * within the
     * function.
     *
     * @param functions
     * @return Duration
     */
    public static Duration[] timeIt(Runnable[] functions) {
        long startingNanoSeconds, endingNanoSeconds;
        Duration[] durations = new Duration[functions.length];
        for (int i = 0; i < functions.length; i++) {
            startingNanoSeconds = System.nanoTime();
            functions[i].run();
            endingNanoSeconds = System.nanoTime();
            durations[i] = Duration.ofNanos((endingNanoSeconds - startingNanoSeconds));
        }
        return durations;
    }

    /**
     * Given a runnable funtion array returns a Duration object array with the mean
     * value of
     * time spent running each function. The amount of times that a function is
     * executed is defined by batcSize. If batchSize is <= 0, returns a Duration of
     * 0.
     *
     * @param functions
     * @param batchSize
     * @return
     */
    public static Duration[] batchTimeIt(Runnable[] functions, int batchSize) {
        if (batchSize <= 0) {
            return (Duration[]) Arrays.stream(new Duration[batchSize]).map(a -> Duration.ofNanos(0)).toArray();
        }

        long startingNanoSeconds, endingNanoSeconds;
        Duration[] durations = new Duration[functions.length];
        long[] function_durations = new long[batchSize];

        for (int i = 0; i < functions.length; i++) {
            for (int j = 0; i < batchSize; i++) {
                startingNanoSeconds = System.nanoTime();
                functions[i].run();
                endingNanoSeconds = System.nanoTime();
                function_durations[j] = endingNanoSeconds - startingNanoSeconds;
            }
            durations[i] = Duration.ofNanos(LongStream.of(function_durations).sum() / batchSize);
        }

        return durations;
    }
}
