package TimeProfiler;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TimeProfiler {

	/**
	 * Given a runnable function, retuns the time that was spent executing that
	 * function
	 *
	 * @param function
	 * @return Time of execution in nanoseconds
	 */
	private static long timeFunction(Runnable function) {
		long startingNanoSeconds = System.nanoTime();
		function.run();
		long endingNanoSeconds = System.nanoTime();
		return (endingNanoSeconds - startingNanoSeconds);
	}

	/**
	 * Given a runnable function returns a TimeResult object with the time that was
	 * spent running that function.
	 *
	 * @param function
	 * @return TimeResult
	 * @see TimeResult
	 */
	public static TimeResult timeIt(Runnable function) {
		return new TimeResult(new Duration[] { Duration.ofNanos(timeFunction(function)) });
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
			return new TimeResult(Stream.generate(() -> Duration.ZERO)
					.limit(1)
					.toArray(Duration[]::new));
		}

		return new TimeResult(LongStream.generate(() -> timeFunction(function))
				.limit(batchSize)
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
		return new TimeResult(LongStream.rangeClosed(0, functions.length - 1)
				.map((x) -> timeFunction(functions[(int) x]))
				.mapToObj(Duration::ofNanos)
				.toArray(Duration[]::new));
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
			return Stream.generate(() -> new TimeResult(new Duration[] { Duration.ZERO }))
					.limit(functions.length)
					.toArray(TimeResult[]::new);
		}

		return Arrays.stream(functions)
				.map(function -> {
					return new TimeResult(Arrays.stream(
							LongStream.generate(() -> timeFunction(function))
									.limit(batchSize)
									.toArray())
							.mapToObj(Duration::ofNanos)
							.toArray(Duration[]::new));
				})
				.toArray(TimeResult[]::new);
	}
}