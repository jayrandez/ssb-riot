package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import demerit.ConnectionData;
import demerit.Model;

/**
 * Model used to connect to the library table to get library-specific data.
 */
public class LibraryModel extends Model {

	public LibraryModel(ConnectionData connectionData) {
		super(connectionData);
	}
	
	public String getLibraryName(Integer libraryId) throws SQLException {
		String queryString = "SELECT name FROM library WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, libraryId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			return result.getString("name");
		}
		else {
			throw new SQLException();
		}
	}
	
	public Boolean hasAdministrator(Integer libraryId) throws SQLException {
		String queryString = "SELECT * FROM user WHERE library_id=? AND administrator=true";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, libraryId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			query.close();
			return true;
		}
		query.close();
		return false;
	}
	
	public HashMap<Integer, String> listLibraries() throws SQLException {
		HashMap<Integer, String> results = new HashMap<Integer, String>();
		String queryString = "SELECT * FROM library";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		
		while(result.next()) {
			results.put(result.getInt("id"), result.getString("name"));
		}
		
		query.close();
		return results;
	}

}
