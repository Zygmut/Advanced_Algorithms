package Controller;

import java.util.Random;
import java.util.function.DoubleSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import Master.MVC;
import Model.Point;
import Request.Body;
import Request.BodyCode;
import Request.Notify;
import Request.Request;
import Request.RequestCode;
import Request.RequestType;

import java.awt.Dimension;

public class Controller implements Notify {

	private MVC hub;
	private Random rng;
	private Point[] data;

	public Controller(MVC mvc) {
		this.hub = mvc;
		this.rng = new Random();
	}

	public Controller(MVC mvc, int seed) {
		this.hub = mvc;
		this.rng = new Random(seed);
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
		double result = Math.abs(rng.nextGaussian() * 0.2 + 0.5);
		while (!bounded(result)) {
			result = Math.abs(rng.nextGaussian() * 0.2 + 0.5);
		}
		return result;
	}
	
	private double getPoisson(double lambda) {
		double L = Math.exp(-lambda);
		int k = 0;
		double p = 1.0;
		do {
			k++;
			p *= rng.nextDouble();
		} while (p > L);
		return k - 1;
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
						() -> getPoisson(0.5),
						this.hub.getModel().getFrameDimension(),
						this.hub.getModel().getPointAmount());
				Body<Point[]> body = new Body<>(RequestType.PUT, BodyCode.DATA, points);
				this.hub.notifyRequest(new Request<>(RequestCode.NEW_DATA, this, body));
			}
			case SEND_DATA -> {
				this.data = (Point[]) request.body.get(BodyCode.DATA);
			}
			case CALC_MIN_DIS -> {
				this.hub.notifyRequest(new Request<>(RequestCode.GET_DATA, this));
				Thread.startVirtualThread(this::calculateMinDistance);
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	private void calculateMinDistance() {
		//Calcular la minima distancia entre dos puntos.
		//Se calculará mediante los puntos x e y de cada punto.

		double minDistance = Double.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			for (int j = i + 1; j < data.length; j++) {
				double distance = Math.sqrt(Math.pow(data[i].x() - data[j].x(), 2) + Math.pow(data[i].y() - data[j].y(), 2));
				System.out.println("Distancia entre " + i + " y " + j + ": " + distance);
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		System.out.println("La distancia mínima entre dos puntos es: " + minDistance);

	}

}
