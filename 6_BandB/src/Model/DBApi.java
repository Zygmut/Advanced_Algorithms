package Model;

import java.sql.Statement;
import java.util.ArrayList;
import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBApi {

	private final String DB_URL;
	private Connection connection;

	public DBApi(String pathToDB) {
		this.connection = null;
		this.DB_URL = "jdbc:sqlite:" + pathToDB;
	}

	public void connect() throws SQLException {
		this.connection = DriverManager.getConnection(DB_URL);
	}

	public void disconnect() throws SQLException {
		this.connection.close();
	}

	public String[] executeQuery(String query, String[] columns) throws SQLException {
		try (Statement stmt = this.connection.createStatement()) {
			stmt.setQueryTimeout(30);

			ArrayList<String> results = new ArrayList<>();
			ResultSet result = stmt.executeQuery(query);
			while (result.next()) {
				for (String column : columns) {
					results.add(result.getString(column));
				}
			}

			return results.toArray(String[]::new);
		}
	}

	public int executeUpdate(String query) throws SQLException {
		Statement stmt = this.connection.createStatement();
		stmt.setQueryTimeout(30);
		int result = stmt.executeUpdate(query);
		stmt.close();
		return result;
	}

	public Statement createStatement() throws SQLException {
		return this.connection.createStatement();
	}

	public void commit() throws SQLException {
		this.connection.commit();
	}

	public void rollback() throws SQLException {
		this.connection.rollback();
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

}
