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
			case SAVE_PUZZLE -> {
				final Object[] params = (Object[]) request.body.content;
				final Board board = (Board) params[0];
				this.savePuzzle(board);
			}
			case GET_LAST_PUZZLE -> {
				this.sendResponse(new Response(ResponseCode.GET_LAST_PUZZLE, this, new Body(getLastPuzzle())));
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

	// This can be used for the shuffle and to display the "current" puzzle
	private Board getLastPuzzle() {
		Gson gson = new Gson();
		try {
			this.dbApi.connect();
			ResultSet result = this.dbApi.executeQuery("SELECT * FROM Puzzle ORDER BY id DESC LIMIT 1");
			if (result.next()) {
				return gson.fromJson(result.getString("puzzle"), Board.class);
			}

			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while saving to DB.", e);
		}

		return null;
	}

	// Any time the puzzle is modified (new size, shuffled), the puzzle will need to be solved
	private void savePuzzle(Board board) {
		Gson gson = new Gson();
		try {
			this.dbApi.connect();

			this.dbApi.executeQuery("INSERT INTO Puzzle (puzzle) VALUES (" + gson.toJson(board) + ")");

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
			// This is somewhat redundant, as we will probably save a lot of boards that won't be used.
			// Anyhow, we can say that this is a feature, as you can generate a lot of boards and use them
			// in some other program :D
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Puzzle(id INTEGER PRIMARY KEY AUTOINCREMENT, puzzle JSON);");
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Solution(id INTEGER PRIMARY KEY AUTOINCREMENT, solution JSON);");
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
