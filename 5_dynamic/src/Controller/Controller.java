package Controller;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import utils.Helpers;
import Services.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.RowFilter.Entry;

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
		Set<String> langs = this.getIdLangs(result);
		PriorityQueue<ExecResultData.Connection> pq = new PriorityQueue<>(
				(a, b) -> Double.compare(a.value(), b.value()));

		ExecResultDataTreeNode root = new ExecResultDataTreeNode("", null);
		return root;
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

	private Map<String, Double> mergeLangScores(Map<String,Double> langScores){

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
				String[][] words = (String[][]) request.body.content;
				lang.put(words[0][0], levenshtein(words[1], words[2]));
				Helpers.syncCount.dec();
			}
			case LEVENSHTEIN -> {
				Object[] parameters = (Object[]) request.body.content;
				if (!(parameters[1] instanceof Map<?, ?>)) {
					Logger.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, "Parameters are not a Map<String, Integer>.");
					return;
				}
				this.lang.clear();
				Map<String, Integer> options = (HashMap<String, Integer>) parameters[1];
				Instant start = Instant.now();
				Map<String, Double> results = this.levenshtein((String[]) parameters[0], options.get("parallel") == 1,
						options.get("batchSize"));
				Duration duration = Duration.between(start, Instant.now());

				ExecResultData[] graphData = resultToGraphData(results);
				ExecResultDataTreeNode treeData = resultToTreeData(results, graphData);

				Body body = new Body(new Object[] { duration, graphData, treeData });
				this.sendRequest(new Request(RequestCode.ADD_RESULT, this, body));
			}
			case GET_ALL_LANGS -> {
				final Object[] parameters = (Object[]) request.body.content;
				String[][] langWords = (String[][]) parameters[0];
				String[] langNames = (String[]) parameters[1];

				Instant start = Instant.now();
				Map<String, Double> result = new HashMap<>();
				for (int i = 0; i < langWords.length; i++) {
					result.put("CUSTOM-" + langNames[i], levenshtein(this.words, langWords[i]));
					result.put(langNames[i] + "-CUSTOM", levenshtein(langWords[i], this.words));
				}

				Map<String, Double> mergedResult = mergeLangScores(result);
				Duration duration = Duration.between(start, Instant.now());

				Body body = new Body(new Object[]{mergedResult, duration});
				// TODO CREATE RESPONSE WITH THE MAP AND DURATION
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
