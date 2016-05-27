package controller.database;

import java.sql.Connection;
import java.sql.DriverManager;

import controller.Urls;
import controller.utils;

public class DBConnection {
	private boolean _autocommit = false;
	private static String url;
	private static String user;
	private static String pass;

	public Connection get() {
		Connection conn = null;
		try {
			url = utils.decryptURL(Urls.databaseUrl.getUrl());
			user = utils.decryptURL(Urls.databaseUser.getUrl());
			pass = utils.decryptURL(Urls.databasePass.getUrl());
			DriverManager.registerDriver(new org.postgresql.Driver());
			conn = DriverManager.getConnection(url, user, pass);
			conn.setAutoCommit(_autocommit);
		} catch (Exception e) {
			System.out.println("DBConnection:get => Error " + e.getMessage());
		}
		return conn;
	}
}