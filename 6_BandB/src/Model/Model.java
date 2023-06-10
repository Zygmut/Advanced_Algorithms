package Model;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Config;

public class Model implements Service {

	private DBApi dbApi;

	public Model() {
		this.dbApi = new DBApi(Config.PATH_TO_DB);
	}

	@Override
	public void start() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CALCULATE -> {
				final Solution sol = (Solution) request.body.content;
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Saved Solution to model");
				this.saveSolution(sol);
			}
			case FETCH_STATS -> {
				this.sendResponse(
						new Response(
								ResponseCode.FETCH_STATS,
								this,
								new Body(getAllSolutions())));
			}
			case CREATE_DB -> {
				this.createDB();
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Created DB.");
			}
			default -> {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	private void saveSolution(Solution sol) {
		final Gson gson = new GsonBuilder()
				.registerTypeAdapter(Duration.class, new DurationTypeAdapter())
				.create();
		try {
			this.dbApi.connect();
			this.dbApi.executeUpdate(
					"INSERT INTO Solution (solution) VALUES ('" +
							gson.toJson(sol) +
							"')");
			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while saving to DB.", e);
		}
	}

	private Solution[] getAllSolutions() {
		ArrayList<Solution> solutions = new ArrayList<>();
		final Gson gson = new GsonBuilder()
				.registerTypeAdapter(Duration.class, new DurationTypeAdapter())
				.create();
		try {
			this.dbApi.connect();

			for (String solution : this.dbApi.executeQuery("SELECT * FROM Solution", new String[] { "solution" })) {
				solutions.add(
						gson.fromJson(solution, Solution.class));
			}

			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while saving to DB.", e);
		}

		return solutions.toArray(Solution[]::new);
	}

	private void createDB() {
		try {
			File file = new File(Config.PATH_TO_DB);
			if (!file.exists()) {
				if (file.createNewFile()) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.INFO, "Created DB file.");
				} else {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.INFO, "DB file already exists.");
				}
			}
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate("DROP TABLE IF EXISTS Solution;");
			this.dbApi.executeUpdate(
					"CREATE TABLE Solution(id INTEGER PRIMARY KEY AUTOINCREMENT, solution JSON);");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while creating DB.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				Logger
						.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		} catch (IOException e) {
			Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while creating DB file.", e);
		}
	}
}
