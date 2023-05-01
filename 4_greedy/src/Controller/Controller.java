package Controller;

import Model.GeoPoint;
import Model.Map;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Comunication.Response.ResponseStatus;
import Services.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

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
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(Level.INFO, "Controller stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CHECK_GEOPOINT -> {
				GeoPoint clickedPoint = (GeoPoint) request.body.content;
			}
			case PARSE_MAP -> {
				Gson gson = new Gson();
				Map map = null;
				String folder = (String) request.body.content;
				try (
					Reader reader = new FileReader(
						"./assets/" + folder.toLowerCase() + "/weighted-data.json"
					)
				) {
					// Convert JSON File to Java Object
					map = gson.fromJson(reader, Map.class);
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage());
				}
				Body mapBody = new Body(map);
				this.sendRequest(new Request(RequestCode.LOAD_MAP, this, mapBody));
				this.sendResponse(new Response(ResponseCode.LOAD_MAP, this, mapBody));
			}
			case HELLO_WORLD -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(
						Level.INFO,
						"The body of the request is: {0}",
						request.body
					);
			}
			default -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	@Override
	public void sendRequest(Request request) {
		try (
			Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)
		) {
			ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream()
			);
			out.writeObject(request);

			ObjectInputStream in = new ObjectInputStream(
				socket.getInputStream()
			);
			Response response = (Response) in.readObject();
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Response: {0}", response);
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.SEVERE, "Error while sending request.", e);
		}
	}

	@Override
	public void sendResponse(Response response) {
		try (
			Socket socket = new Socket(Config.SERVER_HOST, Config.SERVER_PORT)
		) {
			ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream()
			);
			out.writeObject(response);
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
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
