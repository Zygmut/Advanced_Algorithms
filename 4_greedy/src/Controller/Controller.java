package Controller;

import Model.Connection;
import Model.DistanceType;
import Model.Execution;
import Model.GeoPoint;
import Model.Graph;
import Model.Map;
import Model.Node;
import Model.Path;
import Model.Statistics.AtomIteration;
import Model.Statistics.Statistics;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Algorithms;
import utils.Exceptions.GraphException;

public class Controller implements Service {

	private Map map;
	private DistanceType distanceType;
	private Statistics statistics;
	private List<AtomIteration> dataPerIteration;

	public Controller() {
		this.map = null;
		this.distanceType = null;
		this.statistics = null;
		this.dataPerIteration = null;
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
				if (Objects.isNull(content) || !(content instanceof Execution)) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "No geopoints to send.");
					return;
				}
				Execution execution = (Execution) content;
				ArrayList<GeoPoint> geoPoints = execution.geoPoints();
				Algorithms algorithm = execution.algorithm();
				this.distanceType = execution.distanceType();
				if (Objects.isNull(geoPoints) || geoPoints.isEmpty() || Objects.isNull(algorithm)
						|| Objects.isNull(distanceType)) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "Invalid data.");
					return;
				}

				Node[] nodes = geoPoints.stream().map(x -> this.geoPointToNode(graph, x)).toArray(Node[]::new);
				List<Node> path = new ArrayList<>();
				if (geoPoints.size() < 2) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "Not enough geopoints.");
					return;
				}

				this.statistics = new Statistics(this.distanceType, algorithm);
				Instant start = Instant.now();
				// Search path between nodes
				for (int i = 0; i < nodes.length - 1; i++) {
					this.dataPerIteration = new ArrayList<>();
					switch (algorithm) {
						case DIJKSTRA -> path.addAll(dijkstra(graph, nodes[i], nodes[i + 1]));
						case GREEDY -> path.addAll(greedy(graph, nodes[i], nodes[i + 1]));
						default -> path.addAll(dijkstra(graph, nodes[i], nodes[i + 1]));
					}
					this.statistics.addIteration(this.dataPerIteration);
				}
				this.dataPerIteration = null;
				Instant end = Instant.now();
				// Clean path to avoid to finish and start in the same node as the algorithm
				// returns the init node and start, and when it's added to the path it's added
				// twice
				double distance = 0;
				for (int i = 0; i < path.size() - 1; i++) {
					Node actual = path.get(i);
					Node next = path.get(i + 1);
					if (Objects.equals(actual.id(), next.id())) {
						path.remove(i);
						continue;
					}
					distance += actual.geoPoint().distanceTo(next.geoPoint(), this.distanceType);
				}
				Path p = new Path(path, distance);
				this.statistics.setNodes(graph.content().length);
				this.statistics.setSolution(p);
				this.statistics.setTime(end.getNano() - start.getNano());
				int iterations = 0;
				int numberOfVisitedNodes = 0;
				for (int i = 0; i < this.statistics.getDataPerIteration().size(); i++) {
					List<AtomIteration> atomIteration = this.statistics.getDataPerIteration().get(i);
					for (int j = 0; j < atomIteration.size(); j++) {
						AtomIteration atom = atomIteration.get(j);
						iterations += atom.interation();
						numberOfVisitedNodes += atom.numberOfVisitedNodes();
					}
				}
				this.statistics.setIterations(iterations);
				this.statistics.setNumberOfVisitedNodes(numberOfVisitedNodes);

				Body body = new Body(this.statistics);
				this.sendRequest(new Request(RequestCode.STORE_STATISTICS, this, body));

				body = new Body(p);
				this.sendResponse(new Response(ResponseCode.SOLUTION, this, body));
				this.distanceType = null;
				this.statistics = null;
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
			if (clickedPoint.distanceTo(node.geoPoint(), this.distanceType) <= radius) {
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
		int totalIterations = 0;
		long memoryUsed = 0;
		int numberOfVisitedNodes = 0;

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
			final Instant start = Instant.now();
			// Encontramos el nodo no visitado con la menor distancia desde el nodo inicial
			Node currentNode = null;
			double shortestDistance = Double.MAX_VALUE;
			for (int j = 0; j < numNodes; j++) {
				if (!visited[j] && distances[j] < shortestDistance) {
					// currentNode = j;
					currentNode = graph.content()[j];
					shortestDistance = distances[j];
					numberOfVisitedNodes++;
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
							+ currentNode.geoPoint().distanceTo(neighbor.geoPoint(), this.distanceType);
					if (tentativeDistance < distances[idMap.get(neighbor)]) {
						distances[idMap.get(neighbor)] = tentativeDistance;
						previous[idMap.get(neighbor)] = currentNode;
					}
					numberOfVisitedNodes++;
				}
			}
			final Instant end = Instant.now();

			// Guardamos los datos de la iteración
			totalIterations++;
			final long auxMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			memoryUsed += auxMem;
			this.dataPerIteration.add(new AtomIteration(i, end.getNano() - start.getNano(),
					numberOfVisitedNodes, auxMem));
		}

		this.statistics.setMemoryUsed(memoryUsed / totalIterations);

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

	private List<Node> greedy(Graph graph, Node startNode, Node endNode) {
		int totalIterations = 0;
		long memoryUsed = 0;
		int numberOfVisitedNodes = 0;

		PriorityQueue<Node> startQueue = new PriorityQueue<>(
				Comparator.comparingDouble(node -> node.geoPoint().distanceTo(endNode.geoPoint(), this.distanceType)));
		PriorityQueue<Node> endQueue = new PriorityQueue<>(
				Comparator
						.comparingDouble(node -> node.geoPoint().distanceTo(startNode.geoPoint(), this.distanceType)));

		startQueue.add(startNode);
		endQueue.add(endNode);

		java.util.Map<Node, Node> startParents = new HashMap<>();
		java.util.Map<Node, Node> endParents = new HashMap<>();
		java.util.Map<Node, Double> startDistances = new HashMap<>();
		java.util.Map<Node, Double> endDistances = new HashMap<>();

		startDistances.put(startNode, 0.0);
		endDistances.put(endNode, 0.0);

		Node meetingNode = null;

		while (!startQueue.isEmpty() && !endQueue.isEmpty()) {
			Instant startT = Instant.now();

			Node start = startQueue.poll();
			Node end = endQueue.poll();

			if (endParents.containsKey(start) || startParents.containsKey(end)) {
				meetingNode = getMeetingNode(startParents, endParents, start, end);
				break;
			}

			for (Connection connection : start.connections()) {
				Node neighbor = graph.findNodeById(connection.nodeId());
				double cost = startDistances.get(start) + connection.weight();

				if (!startDistances.containsKey(neighbor) || cost < startDistances.get(neighbor)) {
					startParents.put(neighbor, start);
					startDistances.put(neighbor, cost);
					// neighbor.setHeuristicCost(endNode); // set heuristic cost to the goal node
					startQueue.add(neighbor);
				}
				numberOfVisitedNodes++;
			}

			for (Connection connection : end.connections()) {
				Node neighbor = graph.findNodeById(connection.nodeId());
				double cost = endDistances.get(end) + connection.weight();

				if (!endDistances.containsKey(neighbor) || cost < endDistances.get(neighbor)) {
					endParents.put(neighbor, end);
					endDistances.put(neighbor, cost);
					// neighbor.setHeuristicCost(startNode); // set heuristic cost to the start node
					endQueue.add(neighbor);
				}
				numberOfVisitedNodes++;
			}

			Instant endT = Instant.now();

			totalIterations++;
			final long auxMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			memoryUsed += auxMem;
			this.dataPerIteration.add(new AtomIteration(totalIterations, endT.getNano() - startT.getNano(),
					numberOfVisitedNodes, auxMem));
		}

		this.statistics.setMemoryUsed(memoryUsed / totalIterations);

		List<Node> path = new ArrayList<>();
		Node current = meetingNode;

		while (current != null) {
			path.add(current);
			current = startParents.get(current);
		}

		Collections.reverse(path);

		current = endParents.get(meetingNode);

		while (current != null) {
			path.add(current);
			current = endParents.get(current);
		}

		return path;
	}

	private Node getMeetingNode(java.util.Map<Node, Node> startParents, java.util.Map<Node, Node> endParents,
			Node start, Node end) {
		if (startParents.containsKey(end)) {
			return end;
		}

		if (endParents.containsKey(start)) {
			return start;
		}

		for (Node node : startParents.keySet()) {
			if (endParents.containsKey(node)) {
				return node;
			}
		}

		return null;
	}

}
