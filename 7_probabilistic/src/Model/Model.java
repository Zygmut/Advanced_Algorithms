package Model;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import utils.Config;

public class Model implements Service {

	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	private DBApi dbApi;

	public Model() {
		this.dbApi = new DBApi(Config.PATH_TO_DB);
	}

	@Override
	public void start() {
		logger.log(Level.INFO, "Model started.");
	}

	@Override
	public void stop() {
		logger.log(Level.INFO, "Model stopped.");
	}

	@Override
	public void notifyRequest(Request request) {
		switch (request.code) {
			case CHECK_PRIMALITY, GET_FACTORS -> {
				final Result result = (Result) request.body.content;
				this.saveResult(result);
			}
			case FETCH_STATS -> {
				this.sendResponse(new Response(ResponseCode.FETCH_STATS,
						this, new Body(getAllResults())));
			}
			case CREATE_DB -> {
				this.createDB();
				logger.log(Level.INFO, "DB created.");
			}
			default -> {
				logger.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	private void createDB() {
		try {
			File file = new File(Config.PATH_TO_DB);
			if (!file.exists()) {
				if (file.createNewFile()) {
					logger.log(Level.INFO, "Created DB file.");
				} else {
					logger.log(Level.INFO, "DB file already exists.");
				}
			}
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate("DROP TABLE IF EXISTS result;");
			this.dbApi.executeUpdate(
					"CREATE TABLE result(id INTEGER PRIMARY KEY AUTOINCREMENT, time_ms INTEGER);");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while creating DB.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error while creating DB file.", e);
		}
	}

	private void saveResult(Result result) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate("INSERT INTO result(time_ms) VALUES(" + result.time().toMillis() + ");");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while saving result.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}
	}

	private Result[] getAllResults() {
		ArrayList<Result> results = new ArrayList<>();
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			String[] rs = this.dbApi.executeQuery("SELECT * FROM result;", new String[] { "time_ms" });
			for (String r : rs) {
				results.add(new Result(Duration.ofMillis(Long.parseLong(r)), null));
			}
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while getting all results.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return results.toArray(Result[]::new);
	}

}
