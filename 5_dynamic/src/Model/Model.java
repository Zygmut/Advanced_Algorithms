package Model;

import Services.Comunication.Request.Request;
import Services.Service;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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
		populateDataBase();
	}

	private void processLanguage(Statement statement, String language)
		throws SQLException {
		statement.executeUpdate("DROP TABLE IF EXISTS " + language);
		statement.executeUpdate("CREATE TABLE " + language + " (word string)");

		try (
			Scanner dictionaryReader = new Scanner(
				new File(
					Config.DIC_PATH + "complete/" + language + ".dic"
				)
			)
		) {
			ArrayList<String> dictionaryEntries = new ArrayList<>();
			while (dictionaryReader.hasNextLine()) {
				String line = dictionaryReader.nextLine();
				if (line.length() == 0) {
					continue;
				}

				dictionaryEntries.add("(\"" + line + "\")");

				if (dictionaryEntries.size() >= Config.BATCH_SIZE) {
					insertDictionaryEntries(statement, language, dictionaryEntries);
					dictionaryEntries.clear();
				}
			}

			if (!dictionaryEntries.isEmpty()) {
				insertDictionaryEntries(statement, language, dictionaryEntries);
				dictionaryEntries.clear();
			}
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.SEVERE, e.getLocalizedMessage());
		}
	}

	private void insertDictionaryEntries(
		Statement statement,
		String language,
		List<String> dictionaryEntries
	) throws SQLException {
		Logger
			.getLogger(this.getClass().getSimpleName())
			.log(
				Level.INFO,
				"Inserting {0} into {1}",
				new Object[] { dictionaryEntries.size(), language }
			);

		statement.executeUpdate(
			"INSERT INTO " +
			language +
			" VALUES " +
			String.join(", ", dictionaryEntries)
		);
	}

	private void populateDataBase() {
		try (
			Connection connection = DriverManager.getConnection(
				"jdbc:sqlite:" + Config.DB_NAME + ".sqlite"
			);
			Statement statement = connection.createStatement()
		) {
			statement.setQueryTimeout(30);

			for (Language lang : Language.values()) {
				processLanguage(statement, lang.name());
			}
		} catch (Exception e) {
			Logger
				.getLogger(this.getClass().getSimpleName())
				.log(Level.SEVERE, e.getLocalizedMessage());
		}
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
			default -> {
				Logger
					.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			}
		}
	}
}
