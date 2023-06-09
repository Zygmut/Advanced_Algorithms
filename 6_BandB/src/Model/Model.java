package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import utils.Config;

public class Model implements Service {

	private DBApi dbApi;

	public Model() {
		this.dbApi = new DBApi(Config.PATH_TO_DB);
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Model stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case SAVE_STAT -> {
				final Object[] params = (Object[]) request.body.content;
				final Solution sol = (Solution) params[0];
				this.saveSolution(sol);
			}
			case FETCH_STATS -> {
				this.sendResponse(new Response(ResponseCode.FETCH_STATS, this, new Body(getAllSolutions())));
			}
			case CREATE_DB -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Creating DB.");
				this.createDB();
			}
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
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
			this.dbApi.executeQuery("INSERT INTO Solution (solution) VALUES (" + gson.toJson(sol) + ")");
			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
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
			ResultSet result = this.dbApi.executeQuery("SELECT * FROM Solution");
			while (result.next()) {
				solutions.add(gson.fromJson(result.getString("solution"), Solution.class));
			}

			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while saving to DB.", e);
		}

		return solutions.toArray(Solution[]::new);
	}

	private void createDB() {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate("DROP TABLE IF EXISTS Solution;");
			this.dbApi.executeUpdate(
					"CREATE TABLE Solution(id INTEGER PRIMARY KEY AUTOINCREMENT, solution JSON);");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while creating DB.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}
	}
}
