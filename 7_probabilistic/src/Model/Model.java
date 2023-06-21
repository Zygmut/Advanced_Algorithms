package Model;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
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
			case FETCH_STATS -> {
				ArrayList<Object> stats = new ArrayList<>();
				for (TableName table : TableName.values()) {
					if (table == TableName.NEWTON_INTERPOLATION) {
						continue;
					}
					stats.add(getTimesFrom(table));
				}
				stats.add(getNewtonianFunction());

				this.sendResponse(new Response(ResponseCode.FETCH_STATS, this, new Body(stats.toArray())));
			}
			case GET_STORED_KEYS ->
				this.sendResponse(new Response(ResponseCode.GET_STORED_KEYS, this, new Body(this.getAllRSAKeys())));
			case CREATE_DB -> {
				this.createDB();
				logger.info("DB created.");
				this.sendRequest(new Request(RequestCode.POPULATE_DB, this));
			}
			case POPULATE_DB -> this.saveNewtonInterpolation((long[]) request.body.content);
			case CHECK_PRIMALITY -> this.saveResult((Result) request.body.content, TableName.IS_PRIME_RESULT);
			case GET_FACTORS -> this.saveResult((Result) request.body.content, TableName.GET_FACTOR_RESULT);
			case ENCRYPT_TEXT -> this.saveResult((Result) request.body.content, TableName.ENCRYPT_RESULT);
			case DECRYPT_TEXT -> this.saveResult((Result) request.body.content, TableName.DECRYPT_RESULT);
			case GENERATE_RSA_KEYS -> {
				logger.info("Saving RSA keys.");
				final Result res = (Result) request.body.content;
				this.saveResult(res, TableName.RSA_KEY_RESULT);
				this.sendRequest(new Request(RequestCode.GET_STORED_KEYS, this));
			}
			default -> {
				logger.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}

	private void saveResult(Result res, TableName table) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi.executeUpdate(table.getInsertString(res));
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while saving to {0}.", table);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
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

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.ENCRYPT_RESULT + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.ENCRYPT_RESULT
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, time JSON, encryption STRING);");

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.DECRYPT_RESULT + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.DECRYPT_RESULT
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, time JSON, decryption STRING);");

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.NEWTON_INTERPOLATION + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.NEWTON_INTERPOLATION
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, hours INTEGER);");

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.IS_PRIME_RESULT + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.IS_PRIME_RESULT
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, time JSON, is_prime INTEGER);");

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.GET_FACTOR_RESULT + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.GET_FACTOR_RESULT
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, time JSON, factors JSON);");

			this.dbApi.executeUpdate("DROP TABLE IF EXISTS " + TableName.RSA_KEY_RESULT + ";");
			this.dbApi.executeUpdate(
					"CREATE TABLE " + TableName.RSA_KEY_RESULT
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, time JSON, key_pair JSON);");

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

	private void saveNewtonInterpolation(long[] time) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			StringBuilder sb = new StringBuilder();
			for (long t : time) {
				sb.append("(").append(t).append("),");
			}
			sb.deleteCharAt(sb.length() - 1);
			this.dbApi.executeUpdate(
					"INSERT INTO " + TableName.NEWTON_INTERPOLATION + "(hours) VALUES " + sb.toString() + ";");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while saving factor time.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}
	}

	private Duration[] getTimesFrom(TableName table) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);

			final Gson gson = new GsonBuilder()
					.registerTypeAdapter(Duration.class, new DurationTypeAdapter())
					.create();
			final String[] rs = this.dbApi.executeQuery("SELECT * FROM " + table + ";", new String[] { "time" });
			Duration[] durations = Arrays.stream(rs).map(e -> gson.fromJson(e, Duration.class))
					.toArray(Duration[]::new);

			this.dbApi.commit();
			this.dbApi.disconnect();
			return durations;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while getting all results.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return new Duration[0];

	}

	private long[] getNewtonianFunction() {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			String[] rs = this.dbApi.executeQuery("SELECT * FROM " + TableName.NEWTON_INTERPOLATION + ";",
					new String[] { "hours" });
			long[] values = Arrays.stream(rs).mapToLong(Long::parseLong).toArray();
			this.dbApi.commit();
			this.dbApi.disconnect();
			return values;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while getting all factor times.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return new long[0];
	}

	private KeyPair[] getAllRSAKeys() {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);

			final Gson gson = new GsonBuilder()
					.registerTypeAdapter(Duration.class, new DurationTypeAdapter())
					.create();
			final String[] rs = this.dbApi.executeQuery("SELECT * FROM " + TableName.RSA_KEY_RESULT + ";",
					new String[] { "key_pair" });

			KeyPair[] keys = Arrays.stream(rs).map(e -> gson.fromJson(e, KeyPair.class)).toArray(KeyPair[]::new);

			this.dbApi.commit();
			this.dbApi.disconnect();
			return keys;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while getting all RSA keys.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return new KeyPair[0];
	}

}
