package Model;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			case CHECK_PRIMALITY, GET_FACTORS -> {
				final Result result = (Result) request.body.content;
				this.saveResult(result);
			}
			case FETCH_STATS -> {
				Object[] content = new Object[] { getAllResults(), getAllFactorTimes() };
				this.sendResponse(new Response(ResponseCode.FETCH_STATS,
						this, new Body(content)));
			}
			case GET_STORED_KEYS -> {
				KeyPair[] kps = this.getAllRSAKeys();
				for (KeyPair kp : kps) {
					logger.log(Level.INFO, "KeyPair: {0}", kp);
				}
				this.sendResponse(new Response(ResponseCode.GET_STORED_KEYS, this, new Body(kps)));
			}
			case CREATE_DB -> {
				this.createDB();
				logger.info("DB created.");
				this.sendRequest(new Request(RequestCode.POPULATE_DB, this));
			}
			case SAVE_FACTOR_TIME -> {
				final long[] time = (long[]) request.body.content;
				this.saveFactorTime(time);
			}
			case POPULATE_DB -> {
				final long[] time = (long[]) request.body.content;
				this.saveFactorTime(time);
			}
			case GENERATE_RSA_KEYS -> {
				final Result res = (Result) request.body.content;
				logger.info("Saving RSA keys.");
				this.saveRSAKeys((KeyPair) res.result());
				this.saveResult(res);
				this.sendRequest(new Request(RequestCode.GET_STORED_KEYS, this));
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
			this.dbApi.executeUpdate("DROP TABLE IF EXISTS factor_times;");
			this.dbApi.executeUpdate("DROP TABLE IF EXISTS rsa_keys;");
			this.dbApi.executeUpdate(
					"CREATE TABLE result(id INTEGER PRIMARY KEY AUTOINCREMENT, time_ms INTEGER);");
			this.dbApi.executeUpdate(
					"CREATE TABLE factor_times(id INTEGER PRIMARY KEY AUTOINCREMENT, time_hours INTEGER);");
			this.dbApi.executeUpdate(
					"CREATE TABLE rsa_keys(id INTEGER PRIMARY KEY AUTOINCREMENT, public_key TEXT, private_key TEXT);");
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

	private void saveRSAKeys(KeyPair kp) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			this.dbApi
					.executeUpdate("INSERT INTO rsa_keys(public_key, private_key) VALUES('" + kp.publicKey().toString()
							+ "', '" + kp.privateKey().toString() + "');");
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while saving RSA keys.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}
	}

	private void saveFactorTime(long[] time) {
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			StringBuilder sb = new StringBuilder();
			for (long t : time) {
				sb.append("(").append(t).append("),");
			}
			sb.deleteCharAt(sb.length() - 1);
			this.dbApi.executeUpdate("INSERT INTO factor_times(time_hours) VALUES" + sb.toString() + ";");
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

	private long[] getAllFactorTimes() {
		ArrayList<Long> times = new ArrayList<>();
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			String[] rs = this.dbApi.executeQuery("SELECT * FROM factor_times;", new String[] { "time_hours" });
			for (String r : rs) {
				times.add(Long.parseLong(r));
			}
			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error while getting all factor times.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return times.stream().mapToLong(l -> l).toArray();
	}

	private KeyPair[] getAllRSAKeys() {
		ArrayList<KeyPair> keys = new ArrayList<>();
		try {
			this.dbApi.connect();
			this.dbApi.setAutoCommit(false);
			String[] rs = this.dbApi.executeQuery("SELECT * FROM rsa_keys;",
					new String[] { "public_key", "private_key" });
			for (int i = 0; i < rs.length; i += 2) {
				final String[] pubs = rs[i].split("\n");
				assert pubs.length == 2;
				final PublicKey pub = new PublicKey(new BigInteger(pubs[0]), new BigInteger(pubs[1]));

				final String[] privs = rs[i + 1].split("\n");
				assert privs.length == 2;
				final PrivateKey priv = new PrivateKey(new BigInteger(privs[0]), new BigInteger(privs[1]));

				keys.add(new KeyPair(priv, pub));
			}

			this.dbApi.commit();
			this.dbApi.disconnect();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while getting all RSA keys.", e);
			try {
				this.dbApi.rollback();
			} catch (Exception e2) {
				logger.log(Level.SEVERE, "Error while rolling back DB.", e2);
			}
		}

		return keys.toArray(KeyPair[]::new);
	}

}
