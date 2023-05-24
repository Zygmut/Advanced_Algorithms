package Controller;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import utils.Helpers;
import Services.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.ExecResultData;
import Model.ExecResultDataTreeNode;

import java.util.List;

public class Controller implements Service {

	private Map<String, Double> lang;
	private String[] words;

	public Controller() {
		this.lang = new HashMap<>();
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

	private int levenshtein(String source, String target) {
		int m = source.length();
		int n = target.length();

		int[][] memo = new int[m + 1][n + 1];

		for (int i = 0; i <= m; i++) {
			memo[i][0] = i;
		}

		for (int j = 0; j <= n; j++) {
			memo[0][j] = j;
		}

		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (source.charAt(i - 1) == target.charAt(j - 1)) {
					memo[i][j] = memo[i - 1][j - 1]; // No operation needed
					continue;
				}

				int deletion = memo[i - 1][j] + 1;
				int insertion = memo[i][j - 1] + 1;
				int substitution = memo[i - 1][j - 1] + 1;
				memo[i][j] = Math.min(Math.min(deletion, insertion), substitution);
			}
		}

		return memo[m][n];
	}

	private double levenshtein(String[] source, String[] target) {
		double score = 0;
		for (String sourceWord : source) {
			int tmpScore = Integer.MAX_VALUE;
			for (String targetWord : target) {
				tmpScore = Math.min(tmpScore, levenshtein(sourceWord, targetWord));
			}
			score += (double) tmpScore / sourceWord.length();
		}
		return score / source.length;
	}

	private Map<String, Double> levenshtein(String[] languages, boolean isParallel, int batchSize) {
		for (int i = 0; i < languages.length; i++) {
			for (int j = 0; j < languages.length; j++) {
				if (i == j) {
					continue;
				}

				// Increment the counter by 1
				Helpers.syncCount.inc();

				lang.put(languages[i] + "-" + languages[j], 0.0);

				// Create a request to fetch those two languages words, then the calculation
				// will be done within the request
				Body body = new Body(new Object[] { languages[i], languages[j], batchSize });
				Request request = new Request(RequestCode.FETCH_LANGS, this, body);
				this.sendRequest(request);

				// Barrera polling
				if (!isParallel) {
					while (Helpers.syncCount.get() != 0) {
						Helpers.await();
					}
				}
			}
		}

		// Barrera polling
		if (isParallel) {
			while (Helpers.syncCount.get() != 0) {
				Helpers.await();
			}
		}

		// All langs were calculated. Get pairs and calculate the euclidean distance
		// from their scores
		return mergeLangScores(this.lang);
	}

	private double euclideanDistance(double x, double y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	private ExecResultData[] resultToGraphData(Map<String, Double> result) {
		List<ExecResultData> data = new ArrayList<>();

		Set<String> langs = this.getIdLangs(result);

		for (String lang1 : langs) {
			final List<ExecResultData.Connection> connections = new ArrayList<>();
			for (String lang2 : langs) {
				if (lang1.equals(lang2)) {
					continue;
				}

				final String key = String.format("%s-%s", lang1, lang2);
				final String key2 = String.format("%s-%s", lang2, lang1);

				ExecResultData.Connection connection = result.containsKey(key)
						? new ExecResultData.Connection(lang2, result.get(key))
						: new ExecResultData.Connection(lang2, result.get(key2));

				connections.add(connection);
			}
			data.add(new ExecResultData(lang1, connections.toArray(ExecResultData.Connection[]::new)));
		}

		return data.toArray(ExecResultData[]::new);
	}

	private ExecResultDataTreeNode resultToTreeData(Map<String, Double> result, ExecResultData[] graph) {
		List<ExecResultData.Edge> mst = new ArrayList<>();
		List<ExecResultData.Edge> edges = new ArrayList<>();
		for (ExecResultData data : graph) {
			for (ExecResultData.Connection connection : data.connections()) {
				edges.add(new ExecResultData.Edge(data.id(), connection.id(), connection.value()));
			}
		}

		// Sort edges by value in ascending order
		Collections.sort(edges);

		// Create a parent array to track the subset of each node
		Map<String, Integer> parent = new HashMap<>();
		int[] auxParent = new int[graph.length];
		for (int i = 0; i < graph.length; i++) {
			parent.put(graph[i].id(), i);
			auxParent[i] = i;
		}

		int edgeCount = 0;
		int index = 0;

		while (edgeCount < graph.length - 1) {
			ExecResultData.Edge actualEdge = edges.get(index);

			// Find the subset of the source and destination
			int srcParent = findParent(auxParent, parent.get(actualEdge.src()));
			int dstParent = findParent(auxParent, parent.get(actualEdge.dst()));

			// Check if including this edge forms a cycle or not
			if (srcParent != dstParent) {
				mst.add(actualEdge);
				edgeCount++;
				auxParent[srcParent] = dstParent;
			}
			index++;
		}

		// A PARTIR DE AQUI SE TENDRIA QUE HACER EL ARBOL, PER NO SE COMO HACERLO
		System.out.println("MST: " + mst);
		Tree tree = new Tree("root");
		tree.buildTree(mst);
		tree.adjacencyList.put("root", mst);
		// Print the adjacency list representation of the tree
		List<ExecResultDataTreeNode> nodes = new ArrayList<>();
		List<ExecResultData.Edge> edge = tree.adjacencyList.get("root");
		for (ExecResultData.Edge e : edge) {
			List<ExecResultData.Edge> edges1 = tree.adjacencyList.get(e.dst());
			List<ExecResultDataTreeNode> nodes2 = new ArrayList<>();
			for (ExecResultData.Edge e1 : edges1) {
				nodes2.add(new ExecResultDataTreeNode(e1.src(), null));
			}
			nodes.add(new ExecResultDataTreeNode(e.dst(), nodes2.toArray(ExecResultDataTreeNode[]::new)));
		}

		ExecResultDataTreeNode root = new ExecResultDataTreeNode("root", nodes.toArray(ExecResultDataTreeNode[]::new));
		System.out.println(tree.adjacencyList.get("root"));
		// FIN DE LA PARTE QUE NO SE COMO HACER

		return root;
	}

	// PARTE DEL TEST QUE NO SE COMO HACER
	class Tree {
		String root;
		Map<String, List<ExecResultData.Edge>> adjacencyList;

		Tree(String root) {
			this.root = root;
			adjacencyList = new TreeMap<>();
		}

		void addEdge(String source, String destination, double weight) {
			ExecResultData.Edge edge = new ExecResultData.Edge(source, destination, weight);
			adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(edge);
			adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>()).add(edge);
		}

		void buildTree(List<ExecResultData.Edge> edges) {
			for (ExecResultData.Edge edge : edges) {
				addEdge(edge.src(), edge.dst(), edge.weight());
			}
		}
	}
	// FIN DE LA PARTE QUE NO SE COMO HACER

	private int findParent(int[] parent, int vertex) {
		if (parent[vertex] != vertex) {
			parent[vertex] = findParent(parent, parent[vertex]);
		}
		return parent[vertex];
	}

	private Set<String> getIdLangs(Map<String, Double> result) {
		Set<String> langs = new HashSet<>();

		for (Map.Entry<String, Double> entry : result.entrySet()) {
			String[] pair = entry.getKey().split("-");
			langs.add(pair[0]);
			langs.add(pair[1]);
		}

		return langs;
	}

	private Map<String, Double> mergeLangScores(Map<String, Double> langScores) {

		Map<String, Double> scores = new HashMap<>(langScores);
		Map<String, Double> langScore = new HashMap<>();
		while (!scores.entrySet().isEmpty()) {
			final Map.Entry<String, Double> entry1 = scores.entrySet().iterator().next();
			final Double score1 = entry1.getValue();
			scores.remove(entry1.getKey());

			final String[] pair1 = entry1.getKey().split("-");
			final String key2 = String.format("%s-%s", pair1[1], pair1[0]);
			final Double score2 = scores.get(key2);
			scores.remove(key2);

			langScore.put(key2, euclideanDistance(score1, score2));
		}

		return langScore;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void notifyRequest(Request request) {
		switch (request.code) {
			case FETCH_LANGS -> {
				final Object[] paramenters = (Object[]) request.body.content;
				final String langName = (String) paramenters[0];
				final String[] sourceWords = (String[]) paramenters[1];
				final String[] targetWords = (String[]) paramenters[2];
				lang.put(langName, levenshtein(sourceWords, targetWords));
				Helpers.syncCount.dec();
			}
			case LEVENSHTEIN -> {
				final Object[] parameters = (Object[]) request.body.content;
				final String[] langNames = (String[]) parameters[0];
				final Map<String, Integer> options = (HashMap<String, Integer>) parameters[1];

				this.lang.clear();

				final Instant start = Instant.now();
				final Map<String, Double> results = this.levenshtein(langNames, options.get("parallel") == 1,
						options.get("batchSize"));
				final Duration duration = Duration.between(start, Instant.now());

				final ExecResultData[] graphData = resultToGraphData(results);
				final ExecResultDataTreeNode treeData = resultToTreeData(results, graphData);

				final Body body = new Body(new Object[] { duration, graphData, treeData });
				this.sendRequest(new Request(RequestCode.ADD_RESULT, this, body));
			}
			case GET_ALL_LANGS -> {
				final Object[] parameters = (Object[]) request.body.content;
				final String[][] langWords = (String[][]) parameters[0];
				final String[] langNames = (String[]) parameters[1];

				final Instant start = Instant.now();
				Map<String, Double> result = new HashMap<>();

				// We create a division of the data, as we need different words for each
				// iteration.
				// As to not add more complexity to the software, we get double the words and
				// slice it by half.
				for (int i = 0; i < langWords.length; i++) {
					result.put("CUSTOM-" + langNames[i],
							levenshtein(this.words, Arrays.copyOfRange(langWords[i], 0, langWords[i].length / 2)));
					result.put(langNames[i] + "-CUSTOM",
							levenshtein(Arrays.copyOfRange(langWords[i], langWords[i].length / 2, langWords[i].length),
									this.words));
				}

				final Map<String, Double> mergedResult = mergeLangScores(result);
				final Duration duration = Duration.between(start, Instant.now());

				Body body = new Body(new Object[] { duration, mergedResult });
				this.sendResponse(new Response(ResponseCode.GUESS_LANG, this, body));
			}
			case GUESS_LANG -> {
				this.words = ((String) request.body.content).split(" ");
				this.sendRequest(new Request(RequestCode.GET_ALL_LANGS, this));
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}
}
