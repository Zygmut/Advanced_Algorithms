package Controller;

import Model.GeoPoint;
import Model.Graph;
import Model.Map;
import Model.Node;
import Model.Path;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Exceptions.GraphException;

public class Controller implements Service {

	private Map map;

	public Controller() {
		this.map = null;
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
						2.5);
				this.map = null;

				Response response = new Response(
						ResponseCode.CHECK_GEOPOINT,
						this,
						new Body(nextValidGeoPoint));
				this.sendResponse(response);
			}
			case GET_MAP -> {
				this.map = (Map) request.body.content;
			}
			case SEND_GEOPOINTS -> {
				Graph graph = fetchMap().graph();
				Object content = request.body.content;
				if (Objects.isNull(content) || !(content instanceof ArrayList<?>)) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "No geopoints to send.");
					return;
				}
				ArrayList<GeoPoint> geoPoints = (ArrayList<GeoPoint>) content;
				Node[] nodes = geoPoints.stream().map(x -> this.geoPointToNode(graph, x)).toArray(Node[]::new);
				List<Node> path = new ArrayList<>();
				if (geoPoints.size() < 2) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "Not enough geopoints.");
					return;
				}
				// Search path between nodes
				for (int i = 0; i < nodes.length - 1; i++) {
					path.addAll(dijkstra(graph, nodes[i], nodes[i + 1]));
				}
				// Clean path to avoid to finish and start in the same node as the algorithm
				// returns the init node and start, and when it's added to the path it's added
				// twice
				double distance = 0;
				for (int i = 0; i < path.size() - 1; i++) {
					Node actual = path.get(i);
					Node next = path.get(i + 1);
					if (Objects.equals(actual.id(), next.id())) {
						path.remove(i);
					}
					distance += actual.geoPoint().euclideanDistanceTo(next.geoPoint());
				}
				Body body = new Body(new Path(path, distance));
				this.sendResponse(new Response(ResponseCode.SOLUTION, this, body));
			}
			case PARSE_MAP -> {
				Gson gson = new Gson();
				this.map = null;
				String folder = (String) request.body.content;
				try (
						Reader reader = new FileReader(
								"./assets/" +
										folder.toLowerCase() +
										"/weighted-data.json")) {
					// Convert JSON File to Java Object
					this.map = gson.fromJson(reader, Map.class);
				} catch (IOException e) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "Error while parsing map.", e);
				}
				Body mapBody = new Body(this.map);
				this.sendRequest(
						new Request(RequestCode.LOAD_MAP, this, mapBody));
				this.sendResponse(
						new Response(ResponseCode.LOAD_MAP, this, mapBody));
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
			double radius) {
		if (Objects.isNull(graphNodes) || graphNodes.length == 0) {
			throw new GraphException(
					"Graph does not have nodes or is not initialized.");
		}

		for (Node node : graphNodes) {
			if (clickedPoint.euclideanDistanceTo(node.geoPoint()) <= radius) {
				return node.geoPoint();
			}
		}

		return null;
	}

	private Node geoPointToNode(Graph graph, GeoPoint geoPoint) {
		for (Node node : graph.content()) {
			if (node.geoPoint().equals(geoPoint)) {
				return node;
			}
		}
		return null;
	}

	private List<Node> dijkstra(Graph graph, Node startNode, Node endNode) {
		int numNodes = graph.content().length; // Número de nodos en el grafo

		// Creamos un HashMap para guardar el id de cada nodo para acceder a los arrays
		// directamente, además también coincide con el índice del nodo en el arreglo de
		// contenido del grafo
		HashMap<Node, Integer> idMap = new HashMap<>();
		for (int i = 0; i < numNodes; i++) {
			idMap.put(graph.content()[i], i);
		}
		// Arreglo para guardar las distancias más cortas desde el nodo inicial
		double[] distances = new double[numNodes];
		// Arreglo para marcar los nodos visitados
		boolean[] visited = new boolean[numNodes];
		// Arreglo para guardar el nodo anterior en la ruta más corta
		Node[] previous = new Node[numNodes];
		// Lista para guardar la ruta más corta
		List<Node> path = new ArrayList<>();

		// Inicializamos las distancias a un valor grande y el nodo anterior a null
		for (int i = 0; i < numNodes; i++) {
			distances[i] = Double.MAX_VALUE;
			previous[i] = null;
		}

		// La distancia desde el nodo inicial a sí mismo es 0
		distances[idMap.get(startNode)] = 0;

		// Iteramos sobre todos los nodos
		for (int i = 0; i < numNodes - 1; i++) {

			// Encontramos el nodo no visitado con la menor distancia desde el nodo inicial
			Node currentNode = null;
			double shortestDistance = Double.MAX_VALUE;
			for (int j = 0; j < numNodes; j++) {
				if (!visited[j] && distances[j] < shortestDistance) {
					// currentNode = j;
					currentNode = graph.content()[j];
					shortestDistance = distances[j];
				}
			}

			// Marcamos el nodo actual como visitado
			visited[idMap.get(currentNode)] = true;

			// Iteramos sobre los nodos vecinos no visitados del nodo actual
			String[] neighbors = currentNode.neighbors();
			Node[] nodes = new Node[neighbors.length];
			for (int j = 0; j < neighbors.length; j++) {
				nodes[j] = graph.findNodeById(neighbors[j]);
			}

			for (Node neighbor : nodes) {
				if (!visited[idMap.get(neighbor)]) {
					double tentativeDistance = distances[idMap.get(currentNode)]
							+ currentNode.geoPoint().euclideanDistanceTo(neighbor.geoPoint());
					if (tentativeDistance < distances[idMap.get(neighbor)]) {
						distances[idMap.get(neighbor)] = tentativeDistance;
						previous[idMap.get(neighbor)] = currentNode;
					}
				}
			}
		}

		// Construimos la ruta más corta desde el nodo final hacia el nodo inicial
		Node currentNode = endNode;
		while (currentNode != startNode) {
			path.add(currentNode);
			currentNode = previous[idMap.get(currentNode)];
		}
		path.add(startNode);
		Collections.reverse(path); // Invertimos la lista para que esté en orden correcto

		return path;
	}

}
