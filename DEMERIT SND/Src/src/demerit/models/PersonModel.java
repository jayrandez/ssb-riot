package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import demerit.ConnectionData;
import demerit.Core;
import demerit.Model;

/**
 * Model used to associate people with author, actor, producer fields, etc.
 */
public class PersonModel extends Model {
	
	public PersonModel(ConnectionData connectionData) {
		super(connectionData);
	}

	public ArrayList<String> listPeople() throws SQLException {
		ArrayList<String> results = new ArrayList<String>();
		String queryString = "SELECT * FROM person";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			results.add(result.getString("name"));
		}
		return results;
	}
	
	private void personToMedia(String name, Integer mediaId, String table) throws SQLException {
		Integer personId = getPerson(name);
		
		if(personId == null) {
			String queryString = "INSERT INTO person(name) VALUES(?)";
			PreparedStatement query = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
			query.setString(1, name);
			query.executeUpdate();
			personId = HelperModel.getLastInsertId(query);
			query.close();
		}
		
		String queryString2 = "INSERT INTO " + table + "(media_id, person_id) VALUES(?, ?)";
		PreparedStatement query2 = connection.prepareStatement(queryString2);
		query2.setInt(1, mediaId);
		query2.setInt(2, personId);
		query2.executeUpdate();
		query2.close();
	}
	
	public void authorToMedia(String name, Integer mediaId) throws SQLException {
		personToMedia(name, mediaId, "media_author");
	}
	
	public void actorToMedia(String name, Integer mediaId) throws SQLException {
		personToMedia(name, mediaId, "media_actor");
	}
	
	public void producerToMedia(String name, Integer mediaId) throws SQLException {
		personToMedia(name, mediaId, "media_producer");
	}
	
	public void removeMediaPeople(Integer mediaId) throws SQLException {
		String[] queryStrings = {
			"DELETE FROM media_author WHERE media_id = ?",
			"DELETE FROM media_actor WHERE media_id = ?",
			"DELETE FROM media_producer WHERE media_id = ?"
		};
		PreparedStatement query;
		for(int i = 0; i < 3; i++) {
			query = connection.prepareStatement(queryStrings[i]);
			query.setInt(1, mediaId);
			query.executeUpdate();
		}
	}
	
	public Integer getPerson(String name) throws SQLException {
		String queryString = "SELECT * FROM person WHERE name=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, name);
		ResultSet result = query.executeQuery();
		
		if(result.next()) {
			Integer id = result.getInt("id");
			query.close();
			return id;
		}
		else {
			query.close();
			return null;
		}
	}
	
	public static void main(String[] args) throws SQLException {
		ConnectionData cData = new ConnectionData();
		cData.dbname = "bpa2010";
		cData.driver = "mysql";
		cData.host = "devmonger.com";
		cData.pass = "1q2w3e4r5t";
		cData.port = "3306";
		cData.user = "bpa2010-general";
		PersonModel model = new PersonModel(cData);
		
		ArrayList<String> peeps = model.listPeople();
		Core.debug(peeps.toString());
	}
}
