package Controller;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import utils.Helpers;
import Services.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Vector;

public class Controller implements Service {

	private Map<String, Integer> lang;

	public Controller() {
		this.lang = new HashMap<>();
	}

	@Override
	public void start() {
		levenshtein(new String[] { "es", "ca" }, true);
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

	private int levenshtein(String[] source, String[] target) {
		int score = 0;
		for (String sourceWord : source) {
			int tmpScore = Integer.MAX_VALUE;
			for (String targetWord : target) {
				tmpScore = Math.min(tmpScore, levenshtein(sourceWord, targetWord));
			}
			score += tmpScore;
		}
		return score / source.length;
	}

	private Map<String, Double> levenshtein(String[] languages, boolean isParallel) {
		// es-ca
		for (int i = 0; i < languages.length; i++) {
			for (int j = 0; j < languages.length; j++) {
				if (i == j) {
					continue;
				}

				// Increment the counter by 1
				Helpers.syncCount.inc();

				lang.put(languages[i] + "-" + languages[j], 0);

				// Create a request to fetch those two languages words, then the calculation
				// will be done within the request
				Body body = new Body(new String[] { languages[i], languages[j] });
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
		Map<String, Double> langScore = new HashMap<>();
		while (!this.lang.entrySet().isEmpty()) {
			final Map.Entry<String, Integer> entry1 = this.lang.entrySet().iterator().next();
			final Integer score1 = entry1.getValue();
			this.lang.remove(entry1.getKey());

			final String[] pair1 = entry1.getKey().split("-");
			final String key2 = String.format("%s-%s", pair1[1], pair1[0]);
			final Integer score2 = this.lang.get(key2);
			this.lang.remove(key2);

			langScore.put(key2, euclideanDistance(score1, score2));
		}

		return langScore;
	}

	public double euclideanDistance(int x, int y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case FETCH_LANGS -> {
				String[][] words = (String[][]) request.body.content;
				lang.put(words[0][0], levenshtein(words[1], words[2]));
				Helpers.syncCount.dec();
			}
			case LEVENSHTEIN -> {
				String[][] parameters = (String[][]) request.body.content;
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Executing Levenshtein for {0} and {1} with paralel = {2} .",
								new Object[] { parameters[0][0], parameters[0][1], parameters[1][0] });
				Map<String, Double> results = this.levenshtein(parameters[0], Boolean.parseBoolean(parameters[1][0]));
				int a = 0;

			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
