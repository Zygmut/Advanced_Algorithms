package Model;

import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;
import Services.Service;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.Config;

public class Model implements Service {

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
				.log(Level.INFO, "Inserting {0} into {1}", new Object[] { dictionaryEntries.size(), language });

		statement.executeUpdate("INSERT INTO " + language + " VALUES " + String.join(", ", dictionaryEntries));
	}

	private String[] getLanguagesNames() {

		ArrayList<String> languageNames = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/Model/" + Config.DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);
			ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

			while (resultSet.next()) {
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
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, e.getLocalizedMessage());
		}
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Finished the population of the DB");
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
			case GET_LANG_NAMES ->
				this.sendResponse(new Response(ResponseCode.GET_LANG_NAMES, this, new Body(getLanguagesNames())));
			default -> {
				Logger.getLogger(this.getClass().getSimpleName())
						.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}
}
