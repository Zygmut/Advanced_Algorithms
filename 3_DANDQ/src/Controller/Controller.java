package Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

import java.awt.Dimension;
import java.time.Duration;
import java.time.Instant;

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

	// Distribucion de poisson que devuelva valores entre 0 y 1
	private double getPoisson() {
		double randomN;
		double result;

		do {
			randomN = rng.nextDouble();
			result = (Math.exp(-lambda) * Math.pow(lambda, randomN)) / getFactorial((int) randomN);
		} while (!bounded(result));

		return result;
	}

	private long getFactorial(int number) {
		long result = 1;

		for (int factor = 2; factor <= number; factor++) {
			result *= factor;
		}

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
			case GENERATE_POISSON_DATA -> {
				resetRNG();
				Point[] points = generateData(
						this::getPoisson,
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
				// TODO: Change this to the request body system
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

	private void calculateMinDistanceNN() {
		Solution[] solutions = initSolutions(true);
		Instant start = Instant.now();
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double tempDistance = data[i].euclideanDistanceTo(data[j]);
				if (tempDistance < solutions[0].distance() && isNotInPairList(data[i], data[j], true, false)) {
					solutions[0] = new Solution(new PairPoint(data[i], data[j]),
							tempDistance,
							Duration.between(start, Instant.now()).toMillis());
					Arrays.sort(solutions,
							(solution1, solution2) -> Double.compare(solution2.distance(), solution1.distance()));
				}
			}
		}
		for (Solution solution : solutions) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO,
							"Minimum distance found is {0} between points {1} and {2} under {3} milliseconds.",
							new Object[] { solution.distance(), solution.pair().p1(), solution.pair().p2(),
									solution.time() });

			Body<Solution> body1 = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, solution);
			this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MIN, this, body1));
		}

		Object[] objects = new Object[5];
		// TODO: Change this to the request body system
		objects[0] = this.hub.getModel().getData();
		objects[1] = this.hub.getModel().getMinPairPointsListNN();
		objects[2] = this.hub.getModel().getMaxPairPointsListNN();
		objects[3] = this.hub.getModel().getMinPairPointsListNLogN();
		objects[4] = this.hub.getModel().getMaxPairPointsListNLogN();
		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, objects);
		this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MIN_DIS, this, body));
	}

	private void calculateMaxDistanceNN() {
		Solution[] solutions = initSolutions(false);
		Instant start = Instant.now();
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double tempDistance = data[i].euclideanDistanceTo(data[j]);
				if (tempDistance > solutions[0].distance() && isNotInPairList(data[i], data[j], false, false)) {
					solutions[0] = new Solution(new PairPoint(data[i], data[j]),
							tempDistance,
							Duration.between(start, Instant.now()).toMillis());
					Arrays.sort(solutions,
							(solution1, solution2) -> Double.compare(solution1.distance(), solution2.distance()));
				}
			}
		}

		for (Solution solution : solutions) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO,
							"Maximum distance found is {0} between points {1} and {2} under {3} milliseconds.",
							new Object[] { solution.distance(), solution.pair().p1(), solution.pair().p2(),
									solution.time() });

			Body<Solution> body1 = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, solution);
			this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MAX, this, body1));
		}

		Object[] objects = new Object[5];
		// TODO: Change this to the request body system
		objects[0] = this.hub.getModel().getData();
		objects[1] = this.hub.getModel().getMinPairPointsListNN();
		objects[2] = this.hub.getModel().getMaxPairPointsListNN();
		objects[3] = this.hub.getModel().getMinPairPointsListNLogN();
		objects[4] = this.hub.getModel().getMaxPairPointsListNLogN();
		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, objects);
		this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MAX_DIS, this, body));
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

	private void calculateMaxDistanceNLogN() {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));
		Instant start = Instant.now();
		List<Solution> solutionsList = findFarthestNPairs(this.data);
		Instant end = Instant.now();
		for (Solution solution : solutionsList) {
			Body<Solution> body1 = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, solution);
			this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MAX_NLOGN, this, body1));
		}
		System.out.println("Time taken: " + Duration.between(start, end).toMillis() + " milliseconds");

		Object[] objects = new Object[5];
		// TODO: Change this to the request body system
		objects[0] = this.hub.getModel().getData();
		objects[1] = this.hub.getModel().getMinPairPointsListNN();
		objects[2] = this.hub.getModel().getMaxPairPointsListNN();
		objects[3] = this.hub.getModel().getMinPairPointsListNLogN();
		objects[4] = this.hub.getModel().getMaxPairPointsListNLogN();

		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, objects);
		this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MAX_DIS, this, body));
	}

	private void calculateMinDistanceNLogN() {
		this.hub.notifyRequest(new Request<>(RequestCode.GET_SOLUTION_AMOUNT, this));
		Instant start = Instant.now();
		List<Solution> solutionsList = findClosestNPairs(this.data);
		for (Solution solution : solutionsList) {
			Body<Solution> body1 = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, solution);
			this.hub.notifyRequest(new Request<>(RequestCode.NEW_PAIR_DATA_MIN_NLOGN, this, body1));
		}
		Instant end = Instant.now();
		System.out.println("Time taken: " + Duration.between(start, end).toMillis() + " milliseconds");

		Object[] objects = new Object[5];
		// TODO: Change this to the request body system
		objects[0] = this.hub.getModel().getData();
		objects[1] = this.hub.getModel().getMinPairPointsListNN();
		objects[2] = this.hub.getModel().getMaxPairPointsListNN();
		objects[3] = this.hub.getModel().getMinPairPointsListNLogN();
		objects[4] = this.hub.getModel().getMaxPairPointsListNLogN();

		Body<Object[]> body = new Body<>(RequestType.PUT, BodyCode.PAIR_POINTS, objects);
		this.hub.notifyRequest(new Request<>(RequestCode.RESULT_MIN_DIS, this, body));
	}

	private List<Solution> findClosestNPairs(Point[] points) {
		Arrays.sort(points, Comparator.comparingDouble(p -> p.x()));
		List<Solution> closestPairs = new ArrayList<>();
		findClosestNPairsUtil(points, 0, points.length - 1, closestPairs);
		return closestPairs;
	}

	private double findClosestNPairsUtil(Point[] points, int low, int high, List<Solution> closestPairs) {
		if (high - low <= this.stripSize) {
			return bruteForce(points, low, high, closestPairs);
		}
		int mid = (low + high) / 2;
		double leftDist = findClosestNPairsUtil(points, low, mid, closestPairs);
		double rightDist = findClosestNPairsUtil(points, mid + 1, high, closestPairs);
		double d = Math.min(leftDist, rightDist);
		List<Point> strip = new ArrayList<>();
		for (int i = low; i <= high; i++) {
			if (Math.abs(points[i].x() - points[mid].x()) < d) {
				strip.add(points[i]);
			}
		}
		Collections.sort(strip, Comparator.comparingDouble(p -> p.y()));
		for (int i = 0; i < strip.size(); i++) {
			for (int j = i + 1; j < strip.size() && j <= i + 15; j++) {
				double dist = strip.get(i).euclideanDistanceTo(strip.get(j));
				if (dist < d && isNotInPairList(strip.get(i), strip.get(j), true, true)) {
					if (closestPairs.size() < this.nSolutions) {
						closestPairs.add(new Solution(new PairPoint(strip.get(i), strip.get(j)), dist, 0));
						Collections.sort(closestPairs, Comparator.comparingDouble(p -> p.distance()));
					} else if (dist < closestPairs.get(this.nSolutions - 1).distance()) {
						closestPairs.remove(this.nSolutions - 1);
						closestPairs.add(new Solution(new PairPoint(strip.get(i), strip.get(j)), dist, 0));
						Collections.sort(closestPairs, Comparator.comparingDouble(p -> p.distance()));
					}
				}
			}
		}
		return d;
	}

	private double bruteForce(Point[] points, int low, int high, List<Solution> closestPairs) {
		double minDist = Double.MAX_VALUE;
		for (int i = low; i < high; i++) {
			for (int j = i + 1; j <= high; j++) {
				double dist = points[i].euclideanDistanceTo(points[j]);
				if (dist < minDist && isNotInPairList(points[i], points[j], true, true)) {
					if (closestPairs.size() < this.nSolutions) {
						closestPairs.add(new Solution(new PairPoint(points[i], points[j]), dist, 0));
						Collections.sort(closestPairs, Comparator.comparingDouble(p -> p.distance()));
					} else if (dist < closestPairs.get(this.nSolutions - 1).distance()) {
						closestPairs.remove(this.nSolutions - 1);
						closestPairs.add(new Solution(new PairPoint(points[i], points[j]), dist, 0));
						Collections.sort(closestPairs, Comparator.comparingDouble(p -> p.distance()));
					}
					minDist = dist;
				}
			}
		}
		return minDist;
	}

	public List<Solution> findFarthestNPairs(Point[] points) {
		List<Solution> farthestPairs = new ArrayList<>();
		farthestPairs(points, 0, points.length - 1, farthestPairs);
		return farthestPairs;
	}

	private double farthestPairs(Point[] points, int low, int high, List<Solution> farthestPairs) {
		if (high - low + 1 <= this.stripSize) {
			return bruteForceOfMax(points, low, high, farthestPairs);
		}

		int mid = (low + high) / 2;
		double d1 = farthestPairs(points, low, mid, farthestPairs);
		double d2 = farthestPairs(points, mid + 1, high, farthestPairs);
		double d = Math.max(d1, d2);

		Point[] strip = new Point[high - low + 1];
		int j = 0;
		for (int i = low; i <= high; i++) {
			if (Math.abs(points[i].x() - points[mid].x()) <= d) {
				strip[j++] = points[i];
			}
		}
		Arrays.sort(strip, 0, j, Comparator.comparingDouble(p -> p.y()));

		for (int i = 0; i < j; i++) {
			for (int k = i + 1; k < j && (strip[k].y() - strip[i].y()) < d; k++) {
				double dist = strip[i].euclideanDistanceTo(strip[k]);
				if (dist > d && this.isNotInPairList(strip[i], strip[k], false, true)) {
					if (farthestPairs.size() < this.nSolutions) {
						farthestPairs.add(new Solution(new PairPoint(strip[i], strip[k]), dist, 0));
						farthestPairs.sort(Comparator.comparingDouble(Solution::distance).reversed());
					} else if (dist > farthestPairs.get(this.nSolutions - 1).distance()) {
						farthestPairs.remove(this.nSolutions - 1);
						farthestPairs.add(new Solution(new PairPoint(strip[i], strip[k]), dist, 0));
						farthestPairs.sort(Comparator.comparingDouble(Solution::distance).reversed());
					}
					d = farthestPairs.get(this.nSolutions - 1).distance();
				}
			}
		}
		return d;
	}

	private double bruteForceOfMax(Point[] points, int low, int high, List<Solution> farthestPairs) {
		double maxDist = Double.MIN_VALUE;
		for (int i = low; i <= high; i++) {
			for (int j = i + 1; j <= high; j++) {
				double dist = points[i].euclideanDistanceTo(points[j]);
				// Only add if not in the list
				if (this.isNotInPairList(points[i], points[j], false, true)) {
					if (farthestPairs.size() < this.nSolutions) {
						farthestPairs.add(new Solution(new PairPoint(points[i], points[j]), dist, 0));
						farthestPairs.sort(Comparator.comparingDouble(Solution::distance).reversed());
					} else if (dist > farthestPairs.get(this.nSolutions - 1).distance()) {
						farthestPairs.remove(this.nSolutions - 1);
						farthestPairs.add(new Solution(new PairPoint(points[i], points[j]), dist, 0));
						farthestPairs.sort(Comparator.comparingDouble(Solution::distance).reversed());
					}
				}
				maxDist = Math.max(maxDist, dist);
			}
		}
		return maxDist;
	}

}
