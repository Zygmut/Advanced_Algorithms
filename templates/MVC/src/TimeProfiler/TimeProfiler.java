package TimeProfiler;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

public class TimeProfiler {

  /**
   * A ratio that embodies the amount of time wasted with the implementation
   * of runnableFunctions. All calculations use this number to aproximate the real
   * amount of time spent executing a function.
   * @see RunnableFunction
   */
  private double interferenceRatio = 1.0;

  public TimeProfiler(int ratioPrecision) {
    this.interferenceRatio = calculateInterference(ratioPrecision);
    System.out.println("Interference Ratio: " + this.interferenceRatio);
  }

  /**
   * Returns the ratio that embodies the amount of time wasted with the implementation
   * of runnableFunctions. All calculations use this number to aproximate the real
   * amount of time spent executing a function.
   * @see RunnableFunction
   */
  public double getInterferenceRatio() {
    return this.interferenceRatio;
  }

  private void quadraticRatio(int n) {
    int a = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        a = a + (a - 1);
      }
    }
  }

  /**
   * Creates a ratio using statistics to see how much slower is in average
   * the implementation of RunnableFunction to a native execution.
   * As you increase ratioPrecision, the more precise will be the ratio
   * Returns said ratio (Interference ratio).
   *
   * @param ratioPrecision
   * @return double
   * @see RunnableFunction
   */
  private double calculateInterference(int ratioPrecision) {
    Duration[] interferencedTimes = new Duration[ratioPrecision];
    Duration[] nativeTimes = new Duration[ratioPrecision];
    for (int i = 0; i < ratioPrecision; i++) {
      RunnableFunction<Integer> fn = new RunnableFunction<>(
        params -> {
          int a = 0;
          for (int j = 0; j < params[0]; j++) {
            for (int k = 0; k < params[0]; k++) {
              a = a + (a - 1);
            }
          }
        },
        i
      );

      interferencedTimes[i] = timeIt(fn).getData()[0];

      long startingNanoSeconds = System.nanoTime();
      quadraticRatio(ratioPrecision);
      long endingNanoSeconds = System.nanoTime();
      nativeTimes[i] =
        Duration.ofNanos((endingNanoSeconds - startingNanoSeconds));
    }

    double interfenrecedTimeMean = Arrays
      .stream(interferencedTimes)
      .mapToLong(Duration::toNanos)
      .average()
      .getAsDouble();
    double nativeTimeMean = Arrays
      .stream(nativeTimes)
      .mapToLong(Duration::toNanos)
      .average()
      .getAsDouble();
    System.out.println("RunnableFunction: " + interfenrecedTimeMean);
    System.out.println("Native: " + nativeTimeMean);

    return interfenrecedTimeMean / nativeTimeMean;
  }

  /**
   * Given a runnable function returns a TimeResult object with the time that was
   * spent running that function.
   *
   * @param function
   * @return TimeResult
   * @see TimeResult
   */
  public TimeResult timeIt(Runnable function) {
    long startingNanoSeconds = System.nanoTime();
    function.run();
    long endingNanoSeconds = System.nanoTime();
    return new TimeResult(
      new Duration[] {
        Duration.ofNanos(
          (endingNanoSeconds - startingNanoSeconds) / this.interferenceRatio
        ),
      }
    );
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
  public TimeResult batchTimeIt(Runnable function, int batchSize) {
    if (batchSize <= 0) {
      return new TimeResult(
        Stream.generate(() -> Duration.ZERO).limit(1).toArray(Duration[]::new)
      );
    }

    long[] functionDurations = new long[batchSize];
    for (int i = 0; i < batchSize; i++) {
      long startingNanoSeconds = System.nanoTime();
      function.run();
      long endingNanoSeconds = System.nanoTime();
      functionDurations[i] =
        (endingNanoSeconds - startingNanoSeconds) / this.interferenceRatio;
    }

    return new TimeResult(
      Arrays
        .stream(functionDurations)
        .mapToObj(Duration::ofNanos)
        .toArray(Duration[]::new)
    );
  }

  /**
   * Given a runnable array of functions returns a TimeResult array object with
   * the time that was spent running each one of those functions.
   *
   * @param functions
   * @return TimeResult
   * @see TimeResult
   */
  public TimeResult timeIt(Runnable[] functions) {
    Duration[] durations = new Duration[functions.length];
    for (int i = 0; i < functions.length; i++) {
      long startingNanoSeconds = System.nanoTime();
      functions[i].run();
      long endingNanoSeconds = System.nanoTime();
      durations[i] =
        Duration.ofNanos(
          (endingNanoSeconds - startingNanoSeconds) / this.interferenceRatio
        );
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
  public TimeResult[] batchTimeIt(Runnable[] functions, int batchSize) {
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
        functionDurations[j] =
          (endingNanoSeconds - startingNanoSeconds) / this.interferenceRatio;
      }
      durations[i] =
        new TimeResult(
          Arrays
            .stream(functionDurations)
            .mapToObj(Duration::ofNanos)
            .toArray(Duration[]::new)
        );
    }

    return durations;
  }
}
