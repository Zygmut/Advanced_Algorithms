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
	private ArrayList<Solution> solutionsForMax;
	private ArrayList<Solution> solutionsForMin;
	private int nSolutions;

	public Model(MVC mvc) {
		this.hub = mvc;
		this.seed = 27;
		this.frameDimension = new Dimension(100, 100);
		this.pointAmount = 333 + 420 + 69 + 333;
		this.nSolutions = 3;
		this.data = new Point[] {};
		this.solutionsForMax = new ArrayList<>();
		this.solutionsForMin = new ArrayList<>();
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
				this.solutionsForMax.add(sol);
			}
			case NEW_PAIR_DATA_MIN -> {
				Solution sol = (Solution) request.body.get(BodyCode.PAIR_POINTS);
				this.solutionsForMin.add(sol);
			}
			case UPDATE_SOLUTIONS -> {
				this.nSolutions = (int) request.body.get(BodyCode.SOLUTION_AMOUNT);
			}
			case CLEAR_DATA -> {
				this.data = new Point[] {};
				this.solutionsForMax = new ArrayList<>();
				this.solutionsForMin = new ArrayList<>();
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, this.data);
				this.hub.notifyRequest(new Request<>(RequestCode.SHOW_DATA, this, body));
			}
			case CLEAR_SOLUTIONS -> {
				this.solutionsForMax = new ArrayList<>();
				this.solutionsForMin = new ArrayList<>();
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	public PairPoint[] getMaxPairPointsList() {
		Object[] objects = this.solutionsForMax.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public PairPoint[] getMinPairPointsList() {
		Object[] objects = this.solutionsForMin.toArray();
		PairPoint[] pairPoints = new PairPoint[objects.length];
		for (int i = 0; i < objects.length; i++) {
			pairPoints[i] = ((Solution) objects[i]).pair();
		}
		return pairPoints;
	}

	public ArrayList<Solution> getSolutionsForMax() {
		return solutionsForMax;
	}

	public ArrayList<Solution> getSolutionsForMin() {
		return solutionsForMin;
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
