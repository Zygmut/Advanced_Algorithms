package TimeProfiler;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

public class TimeProfiler {

  /**
   * Given a runnable function returns a TimeResult object with the time that was
   * spent running that function.
   *
   * @param function
   * @return TimeResult
   * @see TimeResult
   */
  public static TimeResult timeIt(Runnable function) {
    long startingNanoSeconds = System.nanoTime();
    function.run();
    long endingNanoSeconds = System.nanoTime();
    return new TimeResult(
        new Duration[] {
            Duration.ofNanos(
                (endingNanoSeconds - startingNanoSeconds)),
        });
  }

  /**
   * Given a runnable funtion returns a TimeResult object with all the values of
   * time spent running that function. The amount of times that the function is
   * executed is defined by batchSize. If batchSize is <= 0, returns a Duration of
   * 0.
   *
   * @param function
   * @param batchSize Amount of times that the function will be executed
   * @return TimeResult
   * @see TimeResult
   */
  public static TimeResult batchTimeIt(Runnable function, int batchSize) {
    if (batchSize <= 0) {
      return new TimeResult(
          Stream.generate(() -> Duration.ZERO).limit(1).toArray(Duration[]::new));
    }

    long[] functionDurations = new long[batchSize];
    for (int i = 0; i < batchSize; i++) {
      long startingNanoSeconds = System.nanoTime();
      function.run();
      long endingNanoSeconds = System.nanoTime();
      functionDurations[i] = (endingNanoSeconds - startingNanoSeconds);
    }

    return new TimeResult(
        Arrays
            .stream(functionDurations)
            .mapToObj(Duration::ofNanos)
            .toArray(Duration[]::new));
  }

  /**
   * Given a runnable array of functions returns a TimeResult array object with
   * the time that was spent running each one of those functions.
   *
   * @param functions
   * @return TimeResult
   * @see TimeResult
   */
  public static TimeResult timeIt(Runnable[] functions) {
    Duration[] durations = new Duration[functions.length];
    for (int i = 0; i < functions.length; i++) {
      long startingNanoSeconds = System.nanoTime();
      functions[i].run();
      long endingNanoSeconds = System.nanoTime();
      durations[i] = Duration.ofNanos(endingNanoSeconds - startingNanoSeconds);
    }
    return new TimeResult(durations);
  }

  /**
   * Given a runnable funtion array returns a TimeResult object array with the
   * mean value of time spent running each function. The amount of times that a
   * function is executed is defined by batchSize. If batchSize is <= 0, returns a
   * Duration of 0.
   *
   * @param functions
   * @param batchSize
   * @return TimeResult[]
   * @see TimeResult
   */
  public static TimeResult[] batchTimeIt(Runnable[] functions, int batchSize) {
    if (batchSize <= 0) {
      return Stream
          .generate(() -> new TimeResult(new Duration[] { Duration.ZERO }))
          .limit(functions.length)
          .toArray(TimeResult[]::new);
    }

    TimeResult[] durations = new TimeResult[functions.length];
    long[] functionDurations = new long[batchSize];

    for (int i = 0; i < functions.length; i++) {
      for (int j = 0; j < batchSize; j++) {
        long startingNanoSeconds = System.nanoTime();
        functions[i].run();
        long endingNanoSeconds = System.nanoTime();
        functionDurations[j] = endingNanoSeconds - startingNanoSeconds;
      }
      durations[i] = new TimeResult(
          Arrays
              .stream(functionDurations)
              .mapToObj(Duration::ofNanos)
              .toArray(Duration[]::new));
    }

    return durations;
  }
}
