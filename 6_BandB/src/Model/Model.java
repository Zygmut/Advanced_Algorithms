package Model;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		// Initialize model things here
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
			case GREET -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.INFO, "Model heard {0} say {1}",
								new Object[] { request.origin, request.body.content });
				String message = "Hello " + request.origin + "! I'm the model.";
				this.saveToDB(message);
				this.sendResponse(new Response(ResponseCode.GREET_BACK, this, new Body(message)));
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

	private void saveToDB(String message) {
		try {
			this.dbApi.connect();
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Greeting(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT);");
			message = message.replace("'", "''");
			String query = "INSERT INTO Greeting(message) VALUES('" + message + "');";
			this.dbApi.executeUpdate(query);
			this.dbApi.disconnect();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while saving to DB.", e);
		}
	}

	private void createDB() {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS Strategy(id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT);");
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS RubikCube(id INTEGER PRIMARY KEY AUTOINCREMENT, size INTEGER, configuration TEXT);");
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS RubikCubeStrategy(id INTEGER PRIMARY KEY AUTOINCREMENT, idRubikCube INTEGER, idStrategy INTEGER);");
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS RubikCubeSolution(id INTEGER PRIMARY KEY AUTOINCREMENT, idRubikCube INTEGER, idStrategy INTEGER, solution TEXT, time INTEGER);");
			this.dbApi.executeUpdate(
					"CREATE TABLE IF NOT EXISTS RubikCubeSolutionStep(id INTEGER PRIMARY KEY AUTOINCREMENT, idRubikCubeSolution INTEGER, step INTEGER, move TEXT);");
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
