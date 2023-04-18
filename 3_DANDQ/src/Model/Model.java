package Model;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Request.Body;
import Request.BodyCode;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import Request.RequestType;

import java.awt.Dimension;

public class Model implements Notify {

	private MVC hub;
	private int seed;
	private Dimension frameDimension;
	private int pointAmount;
	private Point[] data;
	private ArrayList<Solution> solutionsForMaxNN;
	private ArrayList<Solution> solutionsForMinNN;
	private ArrayList<Solution> solutionsForMaxNLogN;
	private ArrayList<Solution> solutionsForMinNLogN;
	private int nSolutions;
	private boolean useNLogNAlgorithm;
	private Boolean useMaxOnAuto;

	public Model(MVC mvc) {
		this.hub = mvc;
		this.seed = 27;
		this.frameDimension = new Dimension(100, 100);
		this.pointAmount = 333 + 420 + 69 + 333;
		this.nSolutions = 3;
		this.data = new Point[] {};
		this.solutionsForMaxNN = new ArrayList<>();
		this.solutionsForMinNN = new ArrayList<>();
		this.solutionsForMaxNLogN = new ArrayList<>();
		this.solutionsForMinNLogN = new ArrayList<>();
		this.useNLogNAlgorithm = false;
		this.useMaxOnAuto = false;
	}

	@Override
	public void notifyRequest(Request<?> request) {
		switch (request.code) {
			case NEW_DATA -> {
				this.data = (Point[]) request.body.get(BodyCode.DATA);
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.data);
				this.hub.notifyRequest(new Request<>(RequestCode.SHOW_DATA, this, body));
			}
			case UPDATE_SEED -> {
				this.seed = (int) request.body.get(BodyCode.SEED);
			}
			case UPDATE_AMOUNT -> {
				this.pointAmount = (int) request.body.get(BodyCode.POINT_AMOUNT);
			}
			case GET_DATA -> {
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.data);
				this.hub.notifyRequest(new Request<>(RequestCode.SEND_DATA, this, body));
			}
			case GET_SOLUTION_AMOUNT -> {
				Body<Integer> body = new Body<>(RequestType.PUT, BodyCode.SOLUTION_AMOUNT, this.nSolutions);
				this.hub.notifyRequest(new Request<>(RequestCode.SEND_SOLUTION_AMOUNT, this, body));
			}
			case NEW_PAIR_DATA_MAX -> {
				Solution sol = (Solution) request.body.get(BodyCode.PAIR_POINTS);
				this.solutionsForMaxNN.add(sol);
			}
			case NEW_PAIR_DATA_MIN -> {
				Solution sol = (Solution) request.body.get(BodyCode.PAIR_POINTS);
				this.solutionsForMinNN.add(sol);
			}
			case NEW_PAIR_DATA_MAX_NLOGN -> {
				Solution sol = (Solution) request.body.get(BodyCode.PAIR_POINTS);
				this.solutionsForMaxNLogN.add(sol);
			}
			case NEW_PAIR_DATA_MIN_NLOGN -> {
				Solution sol = (Solution) request.body.get(BodyCode.PAIR_POINTS);
				this.solutionsForMinNLogN.add(sol);
			}
			case UPDATE_SOLUTIONS -> {
				this.nSolutions = (int) request.body.get(BodyCode.SOLUTION_AMOUNT);
			}
			case CLEAR_DATA -> {
				this.data = new Point[] {};
				this.solutionsForMaxNN = new ArrayList<>();
				this.solutionsForMinNN = new ArrayList<>();
				this.solutionsForMaxNLogN = new ArrayList<>();
				this.solutionsForMinNLogN = new ArrayList<>();
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.data);
				this.hub.notifyRequest(new Request<>(RequestCode.SHOW_DATA, this, body));
			}
			case CLEAR_SOLUTIONS -> {
				this.solutionsForMaxNN = new ArrayList<>();
				this.solutionsForMinNN = new ArrayList<>();
				this.solutionsForMaxNLogN = new ArrayList<>();
				this.solutionsForMinNLogN = new ArrayList<>();
			}
			case CHANGE_ALGORITHM -> {
				this.useNLogNAlgorithm = (boolean) request.body.get(BodyCode.DATA);
			}
			case CHANGE_AUTO_MODE -> {
				this.useMaxOnAuto = (Boolean) request.body.get(BodyCode.DATA);
				System.out.println(this.useMaxOnAuto);
			}
			case GET_ALGORITHM -> {
				Body<Boolean> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.useNLogNAlgorithm);
				this.hub.notifyRequest(new Request<>(RequestCode.SEND_ALGORITHM, this, body));
			}
			case GET_AUTO_MODE -> {
				Body<Boolean> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.useMaxOnAuto);
				this.hub.notifyRequest(new Request<>(RequestCode.SEND_AUTO_MODE, this, body));
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	public PairPoint[] getMaxPairPointsListNN() {
		Object[] objects = this.solutionsForMaxNN.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public PairPoint[] getMinPairPointsListNN() {
		Object[] objects = this.solutionsForMinNN.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public PairPoint[] getMaxPairPointsListNLogN() {
		Object[] objects = this.solutionsForMaxNLogN.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public PairPoint[] getMinPairPointsListNLogN() {
		Object[] objects = this.solutionsForMinNLogN.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public ArrayList<Solution> getSolutionsForMaxNN() {
		return solutionsForMaxNN;
	}

	public ArrayList<Solution> getSolutionsForMinNN() {
		return solutionsForMinNN;
	}

	public ArrayList<Solution> getSolutionsForMaxNLogN() {
		return solutionsForMaxNLogN;
	}

	public ArrayList<Solution> getSolutionsForMinNLogN() {
		return solutionsForMinNLogN;
	}

	public int getSeed() {
		return seed;
	}

	public Dimension getFrameDimension() {
		return frameDimension;
	}

	public int getPointAmount() {
		return pointAmount;
	}

	public Point[] getData() {
		return data;
	}

	public int getNSolutions() {
		return nSolutions;
	}

}
