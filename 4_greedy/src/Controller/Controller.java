package Controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.GeoPoint;
import Model.Graph;
import Model.Node;
import Services.Service;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import utils.Config;

public class Controller implements Service {

	public Controller() {
		// Initialize controller things here
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case HELLO_WORLD -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "The body of the request is: {0}", request.body);
			}
			case HELLO_WORLD_2 -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Hello World 2!");
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest(Request request) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(request);

			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Response response = (Response) in.readObject();
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.INFO, "Response: {0}", response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending request.", e);
		}
	}

	@Override
	public void sendResponse(Response response) {
		try (Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(response);
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while sending response.", e);
		}
	}

	// Método que devuelve el punto seleccionado más cerca del mapa de los que
	// existen
	public Node getClosestPoint(GeoPoint clickedPoint, Graph g) {
		if (g == null || g.getPointsCount() == 0) {
			return null;
		}
		Node closestNode = g.getNode(0);
		double closestDistance = distance(clickedPoint, closestNode.getGeoPoint());
	
		for (int i = 1; i < g.getPointsCount(); i++) {
			Node currentNode = g.getNode(i);
			double currentDistance = distance(clickedPoint, currentNode.getGeoPoint());
	
			if (currentDistance < closestDistance) {
				closestNode = currentNode;
				closestDistance = currentDistance;
			}
		}
	
		return closestNode;
	}
	
	private double distance(GeoPoint point1, GeoPoint point2) {
		double xDiff = point1.x() - point2.x();
		double yDiff = point1.y() - point2.y();
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}
	
	
	
}
