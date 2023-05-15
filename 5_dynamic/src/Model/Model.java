package Model;

import Services.Comunication.Request.Request;
import Services.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.SourceDataLine;

public class Model implements Service {

	private final String DB_NAME = "languages";
	private final String DIC_PATH = "./assets/dictionaries/";
	private final int BATCH_SIZE = 1000;

	public Model() {
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME + ".sqlite");
				Statement statement = connection.createStatement()) {
			statement.setQueryTimeout(30);

			for (Language lang : Language.values()) {
				statement.executeUpdate("DROP TABLE IF EXISTS " + lang.name());
				statement.executeUpdate("CREATE TABLE " + lang.name() + " (word string)");
				try (
						Scanner dictionary_reader = new Scanner(
								new File(DIC_PATH + "complete/" + lang.name().toLowerCase() + ".dic"));) {

					ArrayList<String> dictionary_entries = new ArrayList<>();
					while (dictionary_reader.hasNextLine()) {
						if (dictionary_entries.size() >= BATCH_SIZE) {
							// INSERT AND CLEAN
							Logger
									.getLogger(this.getClass().getSimpleName())
									.log(Level.INFO, "Inserting {0} into {1}",
											new Object[] { dictionary_entries.size(), lang.name() });

							statement.executeUpdate("INSERT INTO " + lang.name() + " VALUES "
									+ String.join(", ", dictionary_entries));
							dictionary_entries.clear();
							continue;
						}

						if (dictionary_reader.nextLine().length() == 0){
							continue;
						}

						dictionary_entries.add("(\"" + dictionary_reader.nextLine() + "\")");
					}
				} catch (Exception e) {
					Logger
							.getLogger(this.getClass().getSimpleName())
							.log(Level.SEVERE, e.getLocalizedMessage());
				}
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
