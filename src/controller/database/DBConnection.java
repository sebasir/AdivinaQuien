package controller.database;

import java.sql.Connection;
import java.sql.DriverManager;

import controller.Urls;

public class DBConnection {
	private static final String _user = "postgres";
	private static final String _pass = "Sebasir123";
	private boolean _autocommit = false;

	public Connection get() {
		Connection conn = null;
		try {
			DriverManager.registerDriver(new org.postgresql.Driver());
			conn = DriverManager.getConnection(Urls.databaseServer.getUrl(), _user, _pass);
			conn.setAutoCommit(_autocommit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}