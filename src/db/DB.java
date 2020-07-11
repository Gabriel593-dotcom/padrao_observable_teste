package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {

	private static Connection conn;

	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")) {

			Properties props = new Properties();
			props.load(fs);
			return props;
		}

		catch (IOException e) {
			throw new DBException("Error in try to load database properties. " + e.getMessage());
		}
	}

	public static Connection getConnection() {

		try {
			if (conn == null) {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props);
			}
		}

		catch (SQLException e) {
			throw new DBException("Error in try to connect database. " + e.getMessage());
		}

		return conn;
	}

	public static void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		}

		catch (SQLException e) {
			throw new DBException("Error in try to close connection. " + e.getMessage());
		}
	}

	public static void closeStatement(Statement st) {

		try {
			if (st != null) {
				st.close();
			}
		}

		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
	}

	public static void closeResultSet(ResultSet rs) {

		try {
			if (rs != null) {
				rs.close();
			}
		}

		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
	}
}
