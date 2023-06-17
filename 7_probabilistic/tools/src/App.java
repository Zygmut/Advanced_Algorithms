import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class App {

	public static boolean isPrime(BigInteger number) {
		final BigInteger zero = BigInteger.ZERO;
		final BigInteger two = BigInteger.TWO;

		if (number.compareTo(BigInteger.ONE) <= 0)
			return false;

		if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
			return true;

		if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
			return false;

		final BigInteger sqrt = number.sqrt();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<Boolean>> futures = new ArrayList<>();

		BigInteger i = BigInteger.valueOf(5);
		BigInteger iPlusTwo = i.add(two);
		while (i.compareTo(sqrt) <= 0) {
			final BigInteger currentI = i;
			final BigInteger currentIPlusTwo = iPlusTwo;
			futures.add(executor.submit(() -> {
				return number.mod(currentI).equals(zero) || number.mod(currentIPlusTwo).equals(zero);
			}));
			i = i.add(BigInteger.valueOf(6));
			iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Future<Boolean> future : futures) {
			try {
				if (future.get()) {
					return false;
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public static boolean trialDivision(BigInteger number) {
		final BigInteger zero = BigInteger.ZERO;
		final BigInteger two = BigInteger.TWO;

		if (number.compareTo(BigInteger.ONE) <= 0)
			return false;

		if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
			return true;

		if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
			return false;

		final BigInteger sqrt = number.sqrt();
		BigInteger i = BigInteger.valueOf(5);
		BigInteger iPlusTwo = i.add(two);

		while (i.compareTo(sqrt) <= 0) {
			if (number.mod(i).equals(zero) || number.mod(iPlusTwo).equals(zero))
				return false;
			i = i.add(BigInteger.valueOf(6));
			iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
		}

		return true;
	}

	public static boolean trialDivision2(BigInteger number) {
		final BigInteger zero = BigInteger.ZERO;
		final BigInteger two = BigInteger.TWO;

		if (number.compareTo(BigInteger.ONE) <= 0)
			return false;

		if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
			return true;

		if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
			return false;

		final BigInteger sqrt = number.sqrt();

		int numThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		try {
			BigInteger i = BigInteger.valueOf(5);
			BigInteger iPlusTwo = i.add(two);

			while (i.compareTo(sqrt) <= 0) {
				Future<Boolean>[] futures = new Future[numThreads];

				for (int j = 0; j < numThreads; j++) {
					final BigInteger currentI = i;
					final BigInteger currentIPlusTwo = iPlusTwo;
					futures[j] = executorService.submit(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return number.mod(currentI).equals(zero) || number.mod(currentIPlusTwo).equals(zero);
						}
					});

					i = i.add(BigInteger.valueOf(6));
					iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
				}

				for (Future<Boolean> future : futures) {
					if (future.get()) {
						return false;
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		return true;
	}

	public static boolean trialDivision3(BigInteger number) {
		final BigInteger zero = BigInteger.ZERO;
		final BigInteger two = BigInteger.TWO;

		if (number.compareTo(BigInteger.ONE) <= 0)
			return false;

		if (number.equals(two) || number.equals(BigInteger.valueOf(3)))
			return true;

		if (number.mod(two).equals(zero) || number.mod(BigInteger.valueOf(3)).equals(zero))
			return false;

		final BigInteger sqrt = number.sqrt();
		int numThreads = Runtime.getRuntime().availableProcessors();

		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);

		try {
			BigInteger i = BigInteger.valueOf(5);
			BigInteger iPlusTwo = i.add(two);

			while (i.compareTo(sqrt) <= 0) {
				for (int j = 0; j < numThreads; j++) {
					final BigInteger currentI = i;
					final BigInteger currentIPlusTwo = iPlusTwo;
					completionService.submit(
							() -> number.mod(currentI).equals(zero) || number.mod(currentIPlusTwo).equals(zero));

					i = i.add(BigInteger.valueOf(6));
					iPlusTwo = iPlusTwo.add(BigInteger.valueOf(6));
				}

				for (int j = 0; j < numThreads; j++) {
					Future<Boolean> future = completionService.take();
					if (future.get()) {
						return false;
					}
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		return true;
	}

	private static String getBigIntegerNumber() {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader("number.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			System.out.println("Error reading file" + e);
		}

		// Remove all non-digits and \n
		sb = new StringBuilder(sb.toString().replaceAll("[^\\d]", ""));

		return sb.toString();
	}

	private static final BigInteger TWO = BigInteger.valueOf(2);

	public static boolean millerRabin(BigInteger number, int iterations, int seed) {
		if (number.equals(BigInteger.TWO) || number.equals(BigInteger.valueOf(3))) {
			return true;
		}

		if (number.compareTo(BigInteger.TWO) < 0 || number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
			return false;
		}

		int s = 0;
		BigInteger d = number.subtract(BigInteger.ONE);

		while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
			s++;
			d = d.divide(BigInteger.TWO);
		}

		Random random = new Random(seed);

		for (int i = 0; i < iterations; i++) {
			if (!isMillerRabinWitness(getRandomBase(number, random), number, d, s)) {
				return false;
			}
		}

		return true;
	}

	public static boolean millerRabin2(BigInteger number, int iterations, int seed) {
		if (number.equals(TWO) || number.equals(BigInteger.valueOf(3))) {
			return true;
		}

		if (number.compareTo(TWO) < 0 || number.mod(TWO).equals(BigInteger.ZERO)) {
			return false;
		}

		int s = 0;
		BigInteger d = number.subtract(BigInteger.ONE);

		while (d.mod(TWO).equals(BigInteger.ZERO)) {
			s++;
			d = d.divide(TWO);
		}

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final BigInteger finalD = d;
		final int finalS = s;
		try {
			Random random = new Random(seed);
			for (int i = 0; i < iterations; i++) {
				Future<Boolean> future = executor
						.submit(() -> isMillerRabinWitness(getRandomBase(number, random), number, finalD, finalS));

				try {
					if (!future.get()) {
						return false; // Found a witness, number is composite
					}
				} catch (Exception e) {
					e.printStackTrace(); // Handle any exception that occurred during computation
				}
			}
		} finally {
			executor.shutdown(); // Shutdown the executor service
		}

		return true; // No witnesses found, number is probably prime
	}

	private static boolean isMillerRabinWitness(BigInteger base, BigInteger number, BigInteger d, int s) {
		BigInteger x = base.modPow(d, number);

		if (x.equals(BigInteger.ONE) || x.equals(number.subtract(BigInteger.ONE))) {
			return true; // Witness condition satisfied
		}

		for (int i = 1; i < s; i++) {
			x = x.modPow(TWO, number);

			if (x.equals(BigInteger.ONE)) {
				return false; // Witness condition not satisfied, number is composite
			}

			if (x.equals(number.subtract(BigInteger.ONE))) {
				return true; // Witness condition satisfied
			}
		}

		return false; // Witness condition not satisfied, number is composite
	}

	private static BigInteger getRandomBase(BigInteger number, Random random) {
		BigInteger randomBase;
		do {
			randomBase = new BigInteger(number.bitLength(), random);
		} while (randomBase.compareTo(TWO) < 0 || randomBase.compareTo(number.subtract(TWO)) > 0);
		return randomBase;
	}

	static BigInteger number = new BigInteger(getBigIntegerNumber());

	private static long[] test1(int numberOfExecutions) {
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			isPrime(number);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test2(int numberOfExecutions) {
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			trialDivision(number);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test3(int numberOfExecutions) {
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			trialDivision2(number);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test4(int numberOfExecutions) {
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			trialDivision3(number);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test5(int numberOfExecutions) {
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			isPrimeBigInteger(number);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test6(int numberOfExecutions) {
		final int seed = 1;
		final int iterations = 10;
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			millerRabin(number, iterations, seed);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static long[] test7(int numberOfExecutions) {
		final int seed = 1;
		final int iterations = 10;
		long[] executionTimes = new long[numberOfExecutions];
		for (int i = 0; i < numberOfExecutions; i++) {
			long startTime = System.nanoTime();
			millerRabin2(number, iterations, seed);
			long endTime = System.nanoTime();
			executionTimes[i] = endTime - startTime;
		}
		return executionTimes;
	}

	private static void saveToDB(long[] executionTimes, String testName) {
		final String DB_NAME = "db.sqlite";
		// Create db.sqlite file if it doesn't exist
		File file = new File(DB_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		final String JDBC_DRIVER = "jdbc:sqlite:" + DB_NAME;
		try (Connection conn = DriverManager.getConnection(JDBC_DRIVER)) {
			// Create a statement
			Statement stmt = conn.createStatement();
			// Drop the table if it exists
			String sql = "DROP TABLE IF EXISTS execution_times";
			stmt.executeUpdate(sql);

			// Create a table
			sql = "CREATE TABLE IF NOT EXISTS execution_times (test_name TEXT, execution_time INTEGER)";
			stmt.executeUpdate(sql);
			stmt.close();

			StringBuilder sb = new StringBuilder();
			// Build a string with execution times
			for (long executionTime : executionTimes) {
				// (test_name, execution_time)
				sb.append("(\"").append(testName).append("\", \"").append(executionTime).append("\"),");
			}
			// Remove last comma
			sb.deleteCharAt(sb.length() - 1);

			// Insert execution times into the table
			sql = "INSERT INTO execution_times (test_name, execution_time) VALUES " + sb.toString();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// --------------------------- INIT JVM WARMUP ---------------------------
		long startTime = System.nanoTime();
		boolean isPrime = isPrime(number);
		long endTime = System.nanoTime();
		System.out.println("Is prime: " + isPrime);
		System.out.println("Time to create BigInteger: " + (endTime - startTime) + " ns");
		startTime = System.nanoTime();
		isPrime = trialDivision(number);
		endTime = System.nanoTime();
		System.out.println("Is prime: " + isPrime);
		System.out.println("Time to create BigInteger MIA: " + (endTime - startTime) + " ns");
		startTime = System.nanoTime();
		isPrime = trialDivision2(number);
		endTime = System.nanoTime();
		System.out.println("Is prime: " + isPrime);
		System.out.println("Time to create BigInteger: " + (endTime - startTime) + " ns");
		startTime = System.nanoTime();
		isPrime = trialDivision3(number);
		endTime = System.nanoTime();
		System.out.println("Is prime: " + isPrime);
		System.out.println("Time to create BigInteger: " + (endTime - startTime) + " ns");
		startTime = System.nanoTime();
		isPrime = isPrimeBigInteger(number);
		endTime = System.nanoTime();
		System.out.println("Is prime: " + isPrime);
		System.out.println("Time to create BigInteger: " + (endTime - startTime) + " ns");
		// --------------------------- FINISH JVM WARMUP ---------------------------

		int numberOfExecutions = 10000;
		int seconds = 2;
		sleep(seconds);

		System.out.println("Test 1 (isPrime):");
		long[] test1 = test1(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test1) + " ns" + " Average: " + average(test1) + " ns");
		saveToDB(test1, "isPrime");
		sleep(seconds);

		System.out.println("Test 2 (trialDivision):");
		long[] test2 = test2(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test2) + " ns" + " Average: " + average(test2) + " ns");
		saveToDB(test2, "trialDivision");
		sleep(seconds);

		System.out.println("Test 3 (trialDivision2):");
		long[] test3 = test3(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test3) + " ns" + " Average: " + average(test3) + " ns");
		saveToDB(test3, "trialDivision2");
		sleep(seconds);

		System.out.println("Test 4 (trialDivision3):");
		long[] test4 = test4(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test4) + " ns" + " Average: " + average(test4) + " ns");
		saveToDB(test4, "trialDivision3");
		sleep(seconds);

		System.out.println("Test 5 (isPrimeBigInteger):");
		long[] test5 = test5(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test5) + " ns" + " Average: " + average(test5) + " ns");
		saveToDB(test5, "isPrimeBigInteger");
		sleep(seconds);

		System.out.println("Test 6 (millerRabin):");
		long[] test6 = test6(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test6) + " ns" + " Average: " + average(test6) + " ns");
		saveToDB(test6, "millerRabin");
		sleep(seconds);

		System.out.println("Test 7 (millerRabin2):");
		long[] test7 = test7(numberOfExecutions);
		System.out.println("\tMax: " + getMax(test7) + " ns" + " Average: " + average(test7) + " ns");
		saveToDB(test7, "millerRabin2");
		sleep(seconds);

		createGraphic(test1, test2, test3, test4, test5, test6, test7);
	}

	private static void sleep(int seconds) {
		try {
			Thread.sleep((long) seconds * 1000);
		} catch (InterruptedException e) {
			System.out.println("Interrupted");
		}
	}

	private static boolean isPrimeBigInteger(BigInteger number) {
		return number.isProbablePrime(100);
	}

	private static long getMax(long[] executionTimes) {
		long max = executionTimes[0];
		for (int i = 1; i < executionTimes.length; i++) {
			if (executionTimes[i] > max) {
				max = executionTimes[i];
			}
		}
		return max;
	}

	private static long average(long[] executionTimes) {
		long sum = 0;
		for (int i = 0; i < executionTimes.length; i++) {
			sum += executionTimes[i];
		}
		return sum / executionTimes.length;
	}

	private static long limitNumber(long number) {
		final long MAX = 50000;
		if (number > MAX) {
			return MAX;
		}
		return number;
	}

	private static ChartPanel createChartPanel(XYSeriesCollection dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Execution times", "Number of executions", "Time (ns)",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		return new ChartPanel(chart);
	}

	private static void createGraphic(long[] t1, long[] t2, long[] t3, long[] t4, long[] t5, long[] t6, long[] t7) {
		XYSeries series1 = new XYSeries("isPrime");
		XYSeries series2 = new XYSeries("trialDivision");
		XYSeries series3 = new XYSeries("trialDivision2");
		XYSeries series4 = new XYSeries("trialDivision3");
		XYSeries series5 = new XYSeries("isPrimeBigInteger");
		XYSeries series6 = new XYSeries("millerRabin");
		XYSeries series7 = new XYSeries("millerRabin2");
		for (int i = 0; i < t1.length; i++) {
			series1.add(i, limitNumber(t1[i]));
			series2.add(i, limitNumber(t2[i]));
			series3.add(i, limitNumber(t3[i]));
			series4.add(i, limitNumber(t4[i]));
			series5.add(i, limitNumber(t5[i]));
			series6.add(i, limitNumber(t6[i]));
			series7.add(i, limitNumber(t7[i]));
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		dataset.addSeries(series5);
		dataset.addSeries(series6);
		dataset.addSeries(series7);

		final String seriesTitle = "Average";
		XYSeries series1Avg = new XYSeries(seriesTitle);
		XYSeries series2Avg = new XYSeries(seriesTitle);
		XYSeries series3Avg = new XYSeries(seriesTitle);
		XYSeries series4Avg = new XYSeries(seriesTitle);
		XYSeries series5Avg = new XYSeries(seriesTitle);
		XYSeries series6Avg = new XYSeries(seriesTitle);
		XYSeries series7Avg = new XYSeries(seriesTitle);
		series1Avg.add(0, average(t1));
		series1Avg.add(t1.length, average(t1));
		series2Avg.add(0, average(t2));
		series2Avg.add(t2.length, average(t2));
		series3Avg.add(0, average(t3));
		series3Avg.add(t3.length, average(t3));
		series4Avg.add(0, average(t4));
		series4Avg.add(t4.length, average(t4));
		series5Avg.add(0, average(t5));
		series5Avg.add(t5.length, average(t5));
		series6Avg.add(0, average(t6));
		series6Avg.add(t6.length, average(t6));
		series7Avg.add(0, average(t7));
		series7Avg.add(t7.length, average(t7));

		JFrame frame = new JFrame("Prime number test");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel graphics = new JPanel(new GridLayout(3, 3));
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series1Avg);
		dataset.addSeries(series1);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series2Avg);
		dataset.addSeries(series2);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series3Avg);
		dataset.addSeries(series3);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series4Avg);
		dataset.addSeries(series4);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series5Avg);
		dataset.addSeries(series5);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series6Avg);
		dataset.addSeries(series6);
		graphics.add(createChartPanel(dataset));

		dataset = new XYSeriesCollection();
		dataset.addSeries(series7Avg);
		dataset.addSeries(series7);
		graphics.add(createChartPanel(dataset));

		frame.add(graphics);
		frame.pack();
		frame.setBounds(0, 0, 1200, 800);
		frame.setVisible(true);
	}
}
