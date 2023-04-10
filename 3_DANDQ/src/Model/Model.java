package Model;

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

	public Model(MVC mvc) {
		this.hub = mvc;
		this.seed = 27;
		this.frameDimension = new Dimension(100, 100);
		this.pointAmount = 333 + 420 + 69 + 333;
		this.data = new Point[] {};
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
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
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

}
