package Controller;

import java.awt.Dimension;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
	private final int stripSize = 15;
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
					Thread.startVirtualThread(this::calculateMaxDistanceNLogN);
				} else {
					Thread.startVirtualThread(this::calculateMaxDistanceNN);
				}
			}
			case CALC_STATS -> {

				// this.hub.notifyRequest(new Request<>(RequestCode.GET_STATS_DATA, this));
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
					this.calculateMaxDistanceNLogN();
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
		Object[] statsData = new Object[12];

		// ------ N^2 -----------
		ArrayList<Solution> max = this.hub.getModel().getSolutionsForMaxNN();
		ArrayList<Solution> min = this.hub.getModel().getSolutionsForMinNN();

		// Media de las distancias
		statsData[0] = max.stream().mapToDouble(Solution::distance).average().orElse(0);
		statsData[1] = min.stream().mapToDouble(Solution::distance).average().orElse(0);

		// Maximo y minimo de las maximas distancias
		statsData[2] = max.stream().mapToDouble(Solution::distance).max().orElse(0);
		statsData[3] = max.stream().mapToDouble(Solution::distance).min().orElse(0);

		// Maximo y minimo de las minimas distancias
		statsData[4] = min.stream().mapToDouble(Solution::distance).min().orElse(0);
		statsData[5] = min.stream().mapToDouble(Solution::distance).max().orElse(0);

		// ------ NLogN -----------
		max = this.hub.getModel().getSolutionsForMaxNLogN();
		min = this.hub.getModel().getSolutionsForMinNLogN();

		// Media de las distancias
		statsData[6] = max.stream().mapToDouble(Solution::distance).average().orElse(0);
		statsData[7] = min.stream().mapToDouble(Solution::distance).average().orElse(0);

		// Maximo y minimo de las maximas distancias
		statsData[8] = max.stream().mapToDouble(Solution::distance).max().orElse(0);
		statsData[9] = max.stream().mapToDouble(Solution::distance).min().orElse(0);

		// Maximo y minimo de las minimas distancias
		statsData[10] = min.stream().mapToDouble(Solution::distance).min().orElse(0);
		statsData[11] = min.stream().mapToDouble(Solution::distance).max().orElse(0);

		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, statsData);
		this.hub.notifyRequest(new Request<>(RequestCode.STATS_DATA, this, body));
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
		Solution[] solutions = initSolutions(true);
		PriorityQueue<Solution> minHeap = new PriorityQueue<>(
				(solution1, solution2) -> Double.compare(solution2.distance(), solution1.distance()));
		for (int i = 0; i < solutions.length; i++) {
			minHeap.offer(solutions[i]);
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
		Solution[] topSolutions = new Solution[solutions.length];
		for (int i = solutions.length - 1; i >= 0; i--) {
			topSolutions[i] = minHeap.poll();
		}
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());
		saveSolutions(Arrays.asList(topSolutions), true, false);
	}

	private void calculateMaxDistanceNN() {
		Solution[] solutions = initSolutions(false);
		PriorityQueue<Solution> maxHeap = new PriorityQueue<>(Comparator.comparingDouble(Solution::distance));
		for (int i = 0; i < solutions.length; i++) {
			maxHeap.offer(solutions[i]);
		}
		this.start = Instant.now();
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double tempDistance = data[i].euclideanDistanceTo(data[j]);
				if (tempDistance > maxHeap.peek().distance() && isNotInPairList(data[i], data[j], false, false)) {
					Solution newSolution = new Solution(new PairPoint(data[i], data[j]),
							tempDistance,
							Duration.between(start, Instant.now()).toMillis());
					maxHeap.offer(newSolution);
					maxHeap.poll();
				}
			}
		}
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(new ArrayList<>(maxHeap), false, false);
	}

	private void calculateMaxDistanceNLogN() {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));

		this.start = Instant.now();

		Point[] dataCopy = Arrays.copyOf(this.data, this.data.length);
		Arrays.sort(dataCopy, 0, dataCopy.length, Comparator.comparingDouble(Point::x));

		List<Solution> solutionsList = farthestPairs(0, data.length - 1, dataCopy);
		double maxDistance = solutionsList.get(solutionsList.size() - 1).distance();

		ArrayList<Point> strip = new ArrayList<>();

		// Left side
		double leftThreshold = dataCopy[dataCopy.length / 2].x() - (maxDistance / 2);
		for (int i = 0; dataCopy[i].x() <= leftThreshold; i++) {
			strip.add(dataCopy[i]);
		}

		// Right side
		double rightThreshold = dataCopy[dataCopy.length / 2].x() + (maxDistance / 2);
		for (int i = dataCopy.length - 1; dataCopy[i].x() >= rightThreshold; i--) {
			strip.add(dataCopy[i]);
		}

		strip.sort(Comparator.comparingDouble(Point::y));

		for (int i = 0; i < strip.size(); i++) {
			for (int j = i + 1; j < strip.size(); j++) {
				double tempDistance = strip.get(i).euclideanDistanceTo(strip.get(j));
				if (tempDistance > solutionsList.get(0).distance()
						&& isNotInPairList(strip.get(i), strip.get(j), false, true)) {
					solutionsList.set(0,
							new Solution(new PairPoint(strip.get(i), strip.get(j)),
									tempDistance,
									Duration.between(start, Instant.now()).toMillis()));
					solutionsList
							.sort((solution1, solution2) -> Double.compare(solution1.distance(), solution2.distance()));
				}
			}
		}

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(solutionsList, false, true);
	}

	private List<Solution> farthestPairs(int low, int high, Point[] dataCopy) {
		// TODO: Create an option to aplly this optimization
		if (high - low + 1 <= this.stripSize) {
			return bruteForceOfMax(low, high, dataCopy);
		}

		int mid = (low + high) / 2;

		List<Solution> left = farthestPairs(low, mid, dataCopy);
		List<Solution> right = farthestPairs(mid + 1, high, dataCopy);

		left.addAll(right);

		// Sort by distance
		// TODO: #47 Upgrade this to a N time complexity
		left.sort(Comparator.comparingDouble(Solution::distance));

		// Get the highest distance from the two parts
		return left.subList(this.nSolutions - 1, left.size() - 1);
	}

	private List<Solution> bruteForceOfMax(int low, int high, Point[] dataCopy) {
		Solution[] solutions = initSolutions(false);
		for (int i = low; i <= high; i++) {
			for (int j = i + 1; j <= high; j++) {
				double tempDistance = dataCopy[i].euclideanDistanceTo(dataCopy[j]);
				if (tempDistance > solutions[0].distance() && isNotInPairList(dataCopy[i], dataCopy[j], false, true)) {
					solutions[0] = new Solution(new PairPoint(dataCopy[i], dataCopy[j]),
							tempDistance,
							Duration.between(this.start, Instant.now()).toMillis());
					Arrays.sort(solutions,
							(solution1, solution2) -> Double.compare(solution1.distance(), solution2.distance()));
				}
			}
		}
		return new ArrayList<>(Arrays.asList(solutions));
	}

	private void calculateMinDistanceNLogN() {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));

		this.start = Instant.now();

		Point[] dataCopy = Arrays.copyOf(this.data, this.data.length);
		Arrays.sort(dataCopy, 0, dataCopy.length, Comparator.comparingDouble(Point::x));

		List<Solution> solutionsList = closestPairs(0, data.length - 1, dataCopy);
		double minDistance = solutionsList.get(solutionsList.size() - 1).distance();

		ArrayList<Point> strip = new ArrayList<>();

		// Center slice
		double leftThreshold = dataCopy[dataCopy.length / 2].x() - (minDistance / 2);
		double rightThreshold = dataCopy[dataCopy.length / 2].x() + (minDistance / 2);
		for (Point point : dataCopy) {
			if (point.x() >= leftThreshold && point.x() <= rightThreshold) {
				strip.add(point);
			}
		}

		strip.sort(Comparator.comparingDouble(Point::y));

		for (int i = 0; i < strip.size(); i++) {
			for (int j = i + 1; j < strip.size(); j++) {
				double tempDistance = strip.get(i).euclideanDistanceTo(strip.get(j));
				if (tempDistance < solutionsList.get(0).distance()
						&& isNotInPairList(strip.get(i), strip.get(j), true, true)) {
					solutionsList.set(0,
							new Solution(new PairPoint(strip.get(i), strip.get(j)),
									tempDistance,
									Duration.between(start, Instant.now()).toMillis()));
					solutionsList
							.sort((solution1, solution2) -> Double.compare(solution2.distance(), solution1.distance()));
				}
			}
		}

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Time taken: {0} milliseconds", Duration.between(start, Instant.now()).toMillis());

		saveSolutions(solutionsList, true, true);
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));
	}

	private List<Solution> closestPairs(int low, int high, Point[] dataCopy) {
		// TODO: Create an option to aplly this optimization
		if (high - low + 1 <= this.stripSize) {
			return bruteForceOfMin(low, high, dataCopy);
		}

		int mid = (low + high) / 2;

		List<Solution> left = closestPairs(low, mid, dataCopy);
		List<Solution> right = closestPairs(mid + 1, high, dataCopy);

		left.addAll(right);

		// Sort by distance
		// TODO: #47 Upgrade this to a N time complexity
		left.sort(Comparator.comparingDouble(Solution::distance));

		// Get the highest distance from the two parts
		return left.subList(0, this.nSolutions - 1);
	}

	private List<Solution> bruteForceOfMin(int low, int high, Point[] dataCopy) {
		Solution[] solutions = initSolutions(true);
		for (int i = low; i <= high; i++) {
			for (int j = i + 1; j <= high; j++) {
				double tempDistance = dataCopy[i].euclideanDistanceTo(dataCopy[j]);
				if (tempDistance < solutions[0].distance() && isNotInPairList(dataCopy[i], dataCopy[j], true, true)) {
					solutions[0] = new Solution(new PairPoint(dataCopy[i], dataCopy[j]),
							tempDistance,
							Duration.between(this.start, Instant.now()).toMillis());
					Arrays.sort(solutions,
							(solution1, solution2) -> Double.compare(solution2.distance(), solution1.distance()));
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
			this.calculateMaxDistanceNLogN();
			this.calculateMinDistanceNLogN();
		}
	}

}
