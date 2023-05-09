package Controller;

import Model.GeoPoint;
import Model.Graph;
import Model.Map;
import Model.Node;
import Services.Comunication.Content.Body;
import Services.Comunication.Helpers;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Service;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Exceptions.GraphException;

public class Controller implements Service {

	private Map map;

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

	private Map fetchMap() {
		Request request = new Request(RequestCode.GET_MAP, this);
		this.sendRequest(request);
		// Helpers.await(Objects::isNull, map); No funciona
		while (Objects.isNull(map)) {
			Helpers.await();
		}
		return map;
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CHECK_GEOPOINT -> {
				this.map = null;
				GeoPoint clickedPoint = (GeoPoint) request.body.content;
				Map modelMap = fetchMap();
				Node[] graphNodes = modelMap.graph().content();
				GeoPoint nextValidGeoPoint = checkClosestGeoPoint(
					clickedPoint,
					graphNodes,
					2.5
				);
				this.map = null;

				Response response = new Response(
					ResponseCode.CHECK_GEOPOINT,
					this,
					new Body(nextValidGeoPoint)
				);
				this.sendResponse(response);
			}
			case GET_MAP -> {
				this.map = (Map) request.body.content;
			}
			case SEND_GEOPOINTS -> {
				Graph graph = fetchMap().graph();
				ArrayList<GeoPoint> geoPoints = (ArrayList<GeoPoint>) request.body.content;
				Node[] nodes = geoPoints.stream().map(x -> this.geoPointToNode(graph, x)).toArray(Node[]::new);
				//this.d(graph, nodes[0], nodes[1]);
			}
			case PARSE_MAP -> {
				Gson gson = new Gson();
				this.map = null;
				String folder = (String) request.body.content;
				try (
					Reader reader = new FileReader(
						"./assets/" +
						folder.toLowerCase() +
						"/weighted-data.json"
					)
				) {
					// Convert JSON File to Java Object
					this.map = gson.fromJson(reader, Map.class);
				} catch (IOException e) {
					Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Error while parsing map.", e);
				}
				Body mapBody = new Body(this.map);
				this.sendRequest(
						new Request(RequestCode.LOAD_MAP, this, mapBody)
					);
				this.sendResponse(
						new Response(ResponseCode.LOAD_MAP, this, mapBody)
					);
			}
			default -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	public GeoPoint checkClosestGeoPoint(
		GeoPoint clickedPoint,
		Node[] graphNodes,
		double radius
	) {
		if (Objects.isNull(graphNodes) || graphNodes.length == 0) {
			throw new GraphException(
				"Graph does not have nodes or is not initialized."
			);
		}

		for (Node node : graphNodes) {
			if (clickedPoint.euclideanDistanceTo(node.geoPoint()) <= radius) {
				return node.geoPoint();
			}
		}

		return null;
	}

	private Node geoPointToNode(Graph graph, GeoPoint geoPoint){
		for (Node node : graph.content()) {
			if(node.geoPoint().equals(geoPoint)){
				return node;
			}
		}
		return null;
	}

}
