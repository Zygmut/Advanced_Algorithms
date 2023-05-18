package Controller;

import Services.Comunication.Request.Request;
import Services.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Service {

	public Controller() {
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

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			default -> {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

}
