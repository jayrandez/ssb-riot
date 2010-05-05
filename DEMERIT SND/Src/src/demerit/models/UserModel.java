package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import demerit.ConnectionData;
import demerit.Model;

/**
 * Model used to authenticate users when accessing demerit.
 */
public class UserModel extends Model {

	public UserModel(ConnectionData connectionData) {
		super(connectionData);
	}
	
	public Boolean isAdministrator(String username, Integer library) throws SQLException {
		String queryString = "SELECT administrator FROM user WHERE username=? AND library_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, username);
		query.setInt(2, library);
		ResultSet result = query.executeQuery();
		
		if(result.next()) {
			Boolean send = result.getBoolean("administrator");
			query.close();
			return send;
		}
		else {
			query.close();
			throw new SQLException();
		}
	}
	
	public Integer isValid(String username, String password, Integer library) throws SQLException {
		String salt = getSalt(username, library);
		password = salt + password;
		
		String queryString = "SELECT * FROM user WHERE username=? AND password=SHA1(?) AND library_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, username);
		query.setString(2, password);
		query.setInt(3, library);
		ResultSet result = query.executeQuery();
		
		if(result.next()) {
			Integer id = result.getInt("id");
			query.close();
			return id;
		}
		else {
			query.close();
			return -1;
		}
	}
	
	public void deleteUser(String username, Integer library) throws SQLException {
		String queryString = "DELETE FROM user WHERE username=? AND library_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, username);
		query.setInt(2, library);
		query.executeUpdate();
	}
	
	public void addUser(String username, String password, String salt, Integer library, Boolean administrator) throws SQLException {
		if(listUsers(library).contains(username)) {
			throw new SQLException();
		}
		password = salt + password;
		String queryString = "INSERT INTO user(library_id, username, password, salt, administrator) VALUES(?, ?, SHA1(?), ?, ?)";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, library);
		query.setString(2, username);
		query.setString(3, password);
		query.setString(4, salt);
		query.setBoolean(5, administrator);
		query.executeUpdate();
	}

	private String getSalt(String username, Integer library) throws SQLException {
		String queryString = "SELECT salt FROM user WHERE username=? AND library_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, username);
		query.setInt(2, library);
		ResultSet result = query.executeQuery();

		if(result.last()) {
			String send = result.getString("salt");
			query.close();
			return send;
		}
		else {
			query.close();
			return "";
		}
	}
	
	public ArrayList<String> listUsers(Integer libraryId) throws SQLException {
		ArrayList<String> results = new ArrayList<String>();
		String queryString = "SELECT * FROM user WHERE library_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, libraryId);
		ResultSet result = query.executeQuery();
		
		while(result.next()) {
			results.add(result.getString("username"));
		}
		
		return results;
	}
}
