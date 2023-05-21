package Model;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Service;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Config;

public class Model implements Service {

	private final int nWordsPerLang = 1000;

	public Model() {
	}

	private String[] getRandomWordsOfLength(int wordNumber, int wordLength, String lang) {
		ArrayList<String> words = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);
			ResultSet resultSet = statement
					.executeQuery("SELECT word FROM " + lang + " WHERE LENGTH(word) = " + wordLength
							+ " ORDER BY RANDOM() LIMIT " + wordNumber);

			while (resultSet.next()) {
				words.add(resultSet.getString("word"));
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
			return new String[] {};
		}

		return words.toArray(String[]::new);
	}

	private String[] getRandomWords(int wordNumber, String lang) {

		ArrayList<String> words = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);
			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM " + lang + " ORDER BY RANDOM() LIMIT " + wordNumber);

			while (resultSet.next()) {
				words.add(resultSet.getString("word"));
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
			return new String[] {};
		}

		return words.toArray(String[]::new);
	}

	private void insertDictionaryEntries(Statement statement, String language, List<String> dictionaryEntries)
			throws SQLException {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Inserting {0} into {1}",
						new Object[] { dictionaryEntries.size(), language.toUpperCase() });

		statement.executeUpdate("INSERT INTO " + language + " VALUES " + String.join(", ", dictionaryEntries));
	}

	private String[] getLanguagesNames() {

		ArrayList<String> languageNames = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);
			ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

			while (resultSet.next()) {
				if(resultSet.getString("name").contains("_")){
					continue;
				}
				languageNames.add(resultSet.getString("name"));
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
			return new String[] {};
		}

		return languageNames.toArray(String[]::new);
	}

	private void removeDataBase() {
		File fileToDelete = new File("src/Model/" + Config.DB_NAME + ".sqlite");
		if (!fileToDelete.exists()) {
			return;
		}

		if (!fileToDelete.delete()) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "Error while removing the database");
			return;
		}

		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Removed DataBase");
	}

	private void processLanguage(Statement statement, File languageFile)
			throws SQLException {
		final String languageName = languageFile.getName().substring(0, languageFile.getName().lastIndexOf('.'));
		statement.executeUpdate("DROP TABLE IF EXISTS " + languageName);
		statement.executeUpdate("CREATE TABLE " + languageName + " (word string)");

		try (Scanner dictionaryReader = new Scanner(languageFile)) {
			ArrayList<String> dictionaryEntries = new ArrayList<>();
			while (dictionaryReader.hasNextLine()) {
				String line = dictionaryReader.nextLine();
				if (line.length() == 0) {
					continue;
				}

				dictionaryEntries.add("(\"" + line + "\")");

				if (dictionaryEntries.size() >= Config.BATCH_SIZE) {
					insertDictionaryEntries(statement, languageName, dictionaryEntries);
					dictionaryEntries.clear();
				}
			}

			if (!dictionaryEntries.isEmpty()) {
				insertDictionaryEntries(statement, languageName, dictionaryEntries);
				dictionaryEntries.clear();
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
		}
	}

	private void populateDataBase(String pathToDicts) {
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);

			File dictionaryDir = new File(pathToDicts);
			File[] dictionaries = dictionaryDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dic"));

			if (dictionaries != null) {
				for (File languageFile : dictionaries) {
					processLanguage(statement, languageFile);
				}
			}

			statement.executeUpdate("DROP TABLE IF EXISTS timed_execution");
			statement.executeUpdate("CREATE TABLE timed_execution (id INTEGER PRIMARY KEY AUTOINCREMENT, milis INTEGER)");
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
		}
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Finished the population of the DB");
	}

	private void addTimedExecution(Duration nanos) {
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				PreparedStatement statement = connection.prepareStatement("INSERT INTO timed_execution (milis) VALUES (?)")) {
			statement.setQueryTimeout(30);

			// Insert the new timed execution with the prepared statement
			statement.setLong(1, nanos.toNanos());
			statement.executeUpdate();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
		}
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Added timedExecution of {0} nanos", nanos.toMillis());
	}

	private Long[] getTimedExecution() {
		ArrayList<Long> result = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);

			ResultSet query = statement.executeQuery("SELECT milis FROM timed_execution ORDER BY id");
			while(query.next()){
				result.add(query.getLong("milis"));
			}

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
			return new Long[0];
		}

		return result.toArray(Long[]::new);
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
			case LOAD_DB -> {
				String pathToDicts = (String) request.body.content;
				removeDataBase();
				populateDataBase(pathToDicts);
			}
			case FETCH_LANGS -> {
				final Object[] parameters = (Object[]) request.body.content;
				final int nWords = (int) parameters[2];
				final String sourceLang = (String) parameters[0];
				final String targetLang = (String) parameters[1];
				final String[] sourceWords = getRandomWords(nWords, sourceLang);
				final String[] targetWords = getRandomWords(nWords, targetLang);

				Body body = new Body(
						new Object[] { sourceLang + "-" + targetLang , sourceWords, targetWords });
				Response response = new Response(ResponseCode.FETCH_LANGS, this, body);
				this.sendResponse(response);
			}
			case GET_ALL_LANGS -> {
				final String[] langNames = this.getLanguagesNames();
				ArrayList<String[]> words = new ArrayList<>();
				for (String langName : langNames) {
					words.add(this.getRandomWords(1000, langName));
				}
				Body body = new Body(new Object[] { words.toArray(String[][]::new), langNames });
				Response response = new Response(ResponseCode.GET_ALL_LANGS, this, body);
				this.sendResponse(response);
			}
			case ADD_RESULT, GUESS_LANG ->
				this.addTimedExecution(((Duration) ((Object[]) request.body.content)[0]));
			case GET_LANG_NAMES ->
				this.sendResponse(new Response(ResponseCode.GET_LANG_NAMES, this, new Body(getLanguagesNames())));
			case GET_STATS ->
				this.sendResponse(new Response(ResponseCode.GET_STATS, this, new Body(getTimedExecution())));
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}
}
