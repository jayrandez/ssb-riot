package demerit;

import java.sql.*;

/**
 * The superclass of the model aspect of MVC which are classes that contain
 * code which performs SQL operations on the database.
 */
public class Model {
	
	protected Connection connection;
	protected Boolean sane;
	
	public Model(ConnectionData connectionData) {
		String urlString = "jdbc:";
		urlString += connectionData.driver + "://";
		urlString += connectionData.host + ":";
		urlString += connectionData.port + "/";
		urlString += connectionData.dbname;
		Core.debug("Connecting to Database: " + urlString);
		try {
			connection = DriverManager.getConnection(urlString, connectionData.user, connectionData.pass);
			sane = true;
		} catch (SQLException e) {
			Core.debug("Connection failed.");
			sane = false;
		}
	}
	
	public Boolean isSane() {
		return sane;
	}
}
