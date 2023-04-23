package Controller;

import java.awt.Dimension;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Model.PairPoint;
import Model.Point;
import Model.Solution;
import Request.Body;
import Request.BodyCode;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import Request.RequestType;

public class Controller implements Notify {

	private MVC hub;
	private Random rng;
	private Point[] data;
	private int nSolutions;
	private boolean stop;
	private boolean useNLogNAlgorithm;
	private Boolean useMaxOnAuto;
	private final int stripSize = 7;
	private double lambda;
	private Instant start;

	public Controller(MVC mvc) {
		this.hub = mvc;
		this.rng = new Random();
		this.stop = false;
		this.lambda = 5.5;
	}

	public Controller(MVC mvc, int seed) {
		this.hub = mvc;
		this.rng = new Random(seed);
		this.stop = false;
	}

	public void setSeed(int seed) {
		this.rng.setSeed(seed);
	}

	private Point[] generateData(DoubleSupplier function, Dimension dimension, int amount) {
		Point[] points = new Point[amount];

		for (int i = 0; i < points.length; i++) {
			points[i] = new Point(
					function.getAsDouble() * dimension.width,
					function.getAsDouble() * dimension.height);
		}

		return points;
	}

	private double nextBoundedGaussian() {
		double result;

		do {
			result = Math.abs(rng.nextGaussian() * 0.2 + 0.5);
		} while (!bounded(result));

		return result;
	}

	// Technically, this would be the "shifted exponential distribution"
	private double getExponential() {
		double result;

		do {
			result = -Math.log(1.0 - rng.nextDouble()) / lambda;
		} while (!bounded(result));

		return result;
	}

	private double getBernoulli() {
		double prob = 0.999999;
		double randomN;
		double result;

		do {
			randomN = rng.nextDouble();
			result = Math.pow(prob, randomN) * Math.pow(1 - prob, 1 - randomN);
		} while (!bounded(result));

		return result;
	}

	private boolean bounded(double point) {
		return point < 1.0 && point > 0.0;
	}

	private void resetRNG() {
		this.rng = new Random(this.hub.getModel().getSeed());
	}

	@Override
	public void notifyRequest(Request<?> request) {
		switch (request.code) {
			case GENERATE_UNIFORM_DATA -> {
				resetRNG();
				Point[] points = generateData(
						rng::nextDouble,
						this.hub.getModel().getFrameDimension(),
						this.hub.getModel().getPointAmount());
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, points);
				this.hub.notifyRequest(new Request<>(RequestCode.NEW_DATA, this, body));
			}
			case GENERATE_GAUSSIAN_DATA -> {
				resetRNG();
				Point[] points = generateData(
						this::nextBoundedGaussian,
						this.hub.getModel().getFrameDimension(),
						this.hub.getModel().getPointAmount());
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, points);
				this.hub.notifyRequest(new Request<>(RequestCode.NEW_DATA, this, body));
			}
			case GENERATE_EXPONENTIAL_DATA -> {
				resetRNG();
				this.hub.notifyRequest(new Request<>(RequestCode.GET_LAMBDA, this));
				Point[] points = generateData(
						this::getExponential,
						this.hub.getModel().getFrameDimension(),
						this.hub.getModel().getPointAmount());
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, points);
				this.hub.notifyRequest(new Request<>(RequestCode.NEW_DATA, this, body));
			}
			case GENERATE_BERNOULLI_DATA -> {
				resetRNG();
				Point[] points = generateData(
						this::getBernoulli,
						this.hub.getModel().getFrameDimension(),
						this.hub.getModel().getPointAmount());
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, points);
				this.hub.notifyRequest(new Request<>(RequestCode.NEW_DATA, this, body));
			}

			case SEND_DATA -> {
				this.data = (Point[]) request.body.get(BodyCode.DATA);
			}
			case SEND_ALGORITHM -> {
				this.useNLogNAlgorithm = (Boolean) request.body.get(BodyCode.DATA);
			}
			case SEND_AUTO_MODE -> {
				this.useMaxOnAuto = (Boolean) request.body.get(BodyCode.DATA);
			}
			case SEND_SOLUTION_AMOUNT -> {
				this.nSolutions = (Integer) request.body.get(BodyCode.SOLUTION_AMOUNT);
			}
			case SEND_LAMBDA -> {
				this.lambda = (Double) request.body.get(BodyCode.LAMBDA);
			}
			case CALC_MIN_DIS -> {
				this.hub.notifyRequest(new Request<>(RequestCode.GET_DATA, this));
				this.hub.notifyRequest(new Request<>(RequestCode.GET_ALGORITHM, this));
				if (this.useNLogNAlgorithm) {
					Thread.startVirtualThread(this::calculateMinDistanceNLogN);
				} else {
					Thread.startVirtualThread(this::calculateMinDistanceNN);
				}
			}
			case CALC_MAX_DIS -> {
				this.hub.notifyRequest(new Request<>(RequestCode.GET_DATA, this));
				this.hub.notifyRequest(new Request<>(RequestCode.GET_ALGORITHM, this));
				if (this.useNLogNAlgorithm) {
					// Thread.startVirtualThread(this::calculateMaxDistanceNLogN);
				} else {
					Thread.startVirtualThread(this::calculateMaxDistanceNN);
				}
			}
			case CALC_STATS -> {
				Thread.startVirtualThread(this::calculateStats);
			}
			case CALC_AUTO -> {
				this.stop = false;
				this.hub.notifyRequest(new Request<>(RequestCode.GET_ALGORITHM, this));
				this.hub.notifyRequest(new Request<>(RequestCode.GET_AUTO_MODE, this));
				if (this.useMaxOnAuto == null) {
					Thread.startVirtualThread(this::calculateAutoBechmark);
				} else {
					Thread.startVirtualThread(this::calculateAuto);
				}
			}
			case STOP_AUTO -> {
				this.stop = true;
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	private void calculateAuto() {
		int numOfCalcs = this.hub.getModel().getData().length;
		numOfCalcs = numOfCalcs * numOfCalcs;
		for (int k = 0; k < numOfCalcs - 1 && !this.stop; k++) {
			this.data = this.hub.getModel().getData();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.WARNING, "Thread was interrupted");
				Thread.currentThread().interrupt();
			}
			if (this.useMaxOnAuto.booleanValue()) {
				if (this.useNLogNAlgorithm) {
					// this.calculateMaxDistanceNLogN();
				} else {
					this.calculateMaxDistanceNN();
				}
			} else {
				if (this.useNLogNAlgorithm) {
					this.calculateMinDistanceNLogN();
				} else {
					this.calculateMinDistanceNN();
				}
			}
		}
	}

	private void calculateStats() {
		Object[] statsData = new Object[16];

		// ------ N^2 -----------
		ArrayList<Solution> max = this.hub.getModel().getSolutionsForMaxNN();
		ArrayList<Solution> min = this.hub.getModel().getSolutionsForMinNN();

		// Media de las distancias
		statsData[0] = max.stream().mapToDouble(Solution::distance).average().orElse(0);
		statsData[3] = min.stream().mapToDouble(Solution::distance).average().orElse(0);

		// Maximo y minimo de las maximas distancias
		statsData[1] = max.stream().mapToDouble(Solution::distance).max().orElse(0);
		statsData[2] = max.stream().mapToDouble(Solution::distance).min().orElse(0);

		// Maximo y minimo de las minimas distancias
		statsData[4] = min.stream().mapToDouble(Solution::distance).max().orElse(0);
		statsData[5] = min.stream().mapToDouble(Solution::distance).min().orElse(0);

		// Tiempo media ejecucion
		statsData[12] = max.stream().mapToDouble(Solution::time).average().orElse(0);
		statsData[13] = min.stream().mapToDouble(Solution::time).average().orElse(0);

		// Repetición de puntos
		// ----- N^2 ------
		// statsData[6] = this.calcPointFreq(max); // NEW
		// statsData[7] = this.calcPointFreq(min); // NEW

		// ------ NLogN -----------
		min = this.hub.getModel().getSolutionsForMinNLogN();

		// Media de las distancias
		statsData[9] = min.stream().mapToDouble(Solution::distance).average().orElse(0);

		// Maximo y minimo de las minimas distancias
		statsData[10] = min.stream().mapToDouble(Solution::distance).max().orElse(0);
		statsData[11] = min.stream().mapToDouble(Solution::distance).min().orElse(0);

		// Tiempo media ejecucion
		statsData[15] = min.stream().mapToDouble(Solution::time).average().orElse(0);

		// Repetición de puntos
		// ----- NLogN ------
		// statsData[8] = this.calcPointFreq(min); // NEW
		// statsData[14] = max.stream().mapToDouble(Solution::time).average().orElse(0);

		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, statsData);
		this.hub.notifyRequest(new Request<>(RequestCode.STATS_DATA, this, body));
	}

	public Object[] calcPointFreq(int c) {
		ArrayList<Solution> list = switch (c) {
			case 0 -> this.hub.getModel().getSolutionsForMaxNN();
			case 1 -> this.hub.getModel().getSolutionsForMinNN();
			case 2 -> this.hub.getModel().getSolutionsForMinNLogN();
			default -> throw new IllegalStateException("Unexpected value: " + c);
		};
		HashMap<Point, Integer> map = new HashMap<>();
		for (Solution s : list) {
			Point p = s.pair().p1();
			map.put(p, map.getOrDefault(p, 0) + 1);
			p = s.pair().p2();
			map.put(p, map.getOrDefault(p, 0) + 1);
		}
		int max = 0;
		Point p = null;
		for (Map.Entry<Point, Integer> entry : map.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				p = entry.getKey();
			}
		}
		return new Object[] { p, max };
	}

	private PairPoint[] getList(boolean isMin, boolean isNLogN) {
		if (isMin) {
			if (isNLogN) {
				return this.hub.getModel().getMinPairPointsListNLogN();
			} else {
				return this.hub.getModel().getMinPairPointsListNN();
			}
		} else {
			if (isNLogN) {
				return this.hub.getModel().getMaxPairPointsListNLogN();
			} else {
				return this.hub.getModel().getMaxPairPointsListNN();
			}
		}
	}

	private boolean isNotInPairList(Point point1, Point point2, boolean isMin, boolean isNLogN) {
		PairPoint[] pairPointsList = getList(isMin, isNLogN);
		for (PairPoint pairPoint : pairPointsList) {
			if (pairPoint.p1().equals(point1) && pairPoint.p2().equals(point2)) {
				return false;
			}
		}
		return true;
	}

	private Solution[] initSolutions(boolean isMin) {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));
		Solution[] solutions = new Solution[this.nSolutions];
		for (int i = 0; i < solutions.length; i++) {
			solutions[i] = new Solution(null, isMin ? Double.MAX_VALUE : Double.MIN_VALUE, 0);
		}
		return solutions;
	}

	private void saveSolutions(List<Solution> solutions, boolean isMin, boolean isNlogN) {
		for (Solution solution : solutions) {

			Body<Solution> body1 = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, solution);
			if (isMin) {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO,
								"Minimum distance found is {0} between points {1} and {2} under {3} milliseconds.",
								new Object[] { solution.distance(), solution.pair().p1(), solution.pair().p2(),
										solution.time() });
				if (isNlogN) {
					this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MIN_NLOGN, this, body1));
				} else {
					this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MIN, this, body1));
				}
			} else {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO,
								"Maximum distance found is {0} between points {1} and {2} under {3} milliseconds.",
								new Object[] { solution.distance(), solution.pair().p1(), solution.pair().p2(),
										solution.time() });
				if (isNlogN) {
					this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MAX_NLOGN, this, body1));
				} else {
					this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MAX, this, body1));
				}
			}
		}

		Object[] objects = new Object[5];
		objects[0] = this.hub.getModel().getData();
		objects[1] = this.hub.getModel().getMinPairPointsListNN();
		objects[2] = this.hub.getModel().getMaxPairPointsListNN();
		objects[3] = this.hub.getModel().getMinPairPointsListNLogN();
		objects[4] = this.hub.getModel().getMaxPairPointsListNLogN();
		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, objects);
		if (isMin) {
			this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MIN_DIS, this, body));
		} else {
			this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MAX_DIS, this, body));
		}
	}

	private void calculateMinDistanceNN() {
		PriorityQueue<Solution> minHeap = new PriorityQueue<>(
				Comparator.comparingDouble(Solution::distance).reversed());

		for (Solution solution : initSolutions(true)) {
			minHeap.offer(solution);
		}

		this.start = Instant.now();
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double tempDistance = data[i].euclideanDistanceTo(data[j]);
				if (tempDistance < minHeap.peek().distance() && isNotInPairList(data[i], data[j], true, false)) {
					minHeap.poll();
					minHeap.offer(new Solution(new PairPoint(data[i], data[j]), tempDistance,
							Duration.between(start, Instant.now()).toMillis()));
				}
			}
		}

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(new ArrayList<>(minHeap), true, false);
	}

	private void calculateMaxDistanceNN() {
		PriorityQueue<Solution> maxHeap = new PriorityQueue<>(Comparator.comparingDouble(Solution::distance));

		for (Solution solution : initSolutions(false)) {
			maxHeap.offer(solution);
		}

		this.start = Instant.now();
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double tempDistance = data[i].euclideanDistanceTo(data[j]);
				if (tempDistance > maxHeap.peek().distance() && isNotInPairList(data[i], data[j], false, false)) {
					maxHeap.poll();
					maxHeap.offer(new Solution(new PairPoint(data[i], data[j]),
							tempDistance,
							Duration.between(start, Instant.now()).toMillis()));
				}
			}
		}

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(new ArrayList<>(maxHeap), false, false);
	}

	private void calculateMinDistanceNLogN() {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));

		this.start = Instant.now();

		Point[] dataCopy = Arrays.copyOf(this.data, this.data.length);
		Arrays.sort(dataCopy, Comparator.comparingDouble(Point::x));

		List<Solution> solutionsList = closestPairs(0, 100, dataCopy);

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(solutionsList, true, true);
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));
	}

	private List<Solution> closestPairs(int low, int high, Point[] dataCopy) {
		// TODO: Create an option to aplly this optimization
		if (high - low + 1 <= this.stripSize) {
			ArrayList<Point> dataSlice = new ArrayList<>();
			for (Point point : dataCopy) {
				if (point.x() > low && point.x() < high) {
					dataSlice.add(point);
				}
			}

			return bruteForceOfMin(dataSlice);
		}

		int mid = (low + high) / 2;

		List<Solution> left = closestPairs(low, mid, dataCopy);
		List<Solution> right = closestPairs(mid, high, dataCopy);

		left.addAll(right);

		// Sort by distance
		// TODO: #47 Upgrade this to a N time complexity
		left.sort(Comparator.comparingDouble(Solution::distance));

		// Get the least distance from the two parts
		List<Solution> solutionsList = left.subList(0, this.nSolutions);

		double minDistance = solutionsList.get(0).distance();

		ArrayList<Point> strip = new ArrayList<>();

		// Center slice
		double leftThreshold = mid - minDistance;
		double rightThreshold = mid + minDistance;
		for (Point point : dataCopy) {
			if (point.x() > leftThreshold && point.x() < rightThreshold) {
				strip.add(point);
			}
		}

		strip.sort(Comparator.comparingDouble(Point::y));

		for (int i = 0; i < strip.size(); i++) {
			for (int j = i + 1; j < strip.size(); j++) {
				double tempDistance = strip.get(i).euclideanDistanceTo(strip.get(j));
				if (tempDistance < solutionsList.get(solutionsList.size() - 1).distance()
						&& isNotInPairList(strip.get(i), strip.get(j), true, true)) {
					solutionsList.set(solutionsList.size() - 1,
							new Solution(new PairPoint(strip.get(i), strip.get(j)),
									tempDistance,
									Duration.between(start, Instant.now()).toMillis()));
					solutionsList.sort(Comparator.comparingDouble(Solution::distance));
				}
			}
		}

		return solutionsList;
	}

	private List<Solution> bruteForceOfMin(ArrayList<Point> dataCopy) {
		Solution[] solutions = initSolutions(true);
		for (int i = 0; i < dataCopy.size(); i++) {
			for (int j = i + 1; j < dataCopy.size(); j++) {
				double tempDistance = dataCopy.get(i).euclideanDistanceTo(dataCopy.get(j));
				if (tempDistance < solutions[solutions.length - 1].distance()
						&& isNotInPairList(dataCopy.get(i), dataCopy.get(j), true, true)) {
					solutions[solutions.length - 1] = new Solution(new PairPoint(dataCopy.get(i), dataCopy.get(j)),
							tempDistance,
							Duration.between(this.start, Instant.now()).toMillis());
					Arrays.sort(solutions, Comparator.comparingDouble(Solution::distance));
				}
			}
		}
		return new ArrayList<>(Arrays.asList(solutions));
	}

	private void calculateAutoBechmark() {
		int numOfCalcs = this.hub.getModel().getData().length;
		numOfCalcs = numOfCalcs * numOfCalcs;
		this.data = this.hub.getModel().getData();
		for (int k = 0; k < numOfCalcs - 1 && !this.stop; k++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.WARNING, "Thread was interrupted");
				Thread.currentThread().interrupt();
			}
			// NN
			this.calculateMaxDistanceNN();
			this.calculateMinDistanceNN();
			// NLogN
			// this.calculateMaxDistanceNLogN();
			this.calculateMinDistanceNLogN();
		}
	}

}
