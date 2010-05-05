package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import demerit.ConnectionData;
import demerit.Core;
import demerit.MediaData;
import demerit.Model;

/**
 * Important model used to connect to the media table.
 * Can be used to insert media and make various connections to other tables
 * for normalization.
 */
public class MediaModel extends Model {

	public MediaModel(ConnectionData connectionData) {
		super(connectionData);
	}
	
	public ArrayList<Integer> searchByAuthor(String searchString) throws SQLException {
		ArrayList<Integer> authorIds = new ArrayList<Integer>();
		ArrayList<Integer> results = new ArrayList<Integer>();
		String queryString = "SELECT * FROM person WHERE name LIKE '%" + searchString + "%'";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			authorIds.add(result.getInt("id"));
		}
		if(authorIds.size() > 0) {
			String queryString2 = "SELECT media_id FROM media_author WHERE ";
			for(Integer id: authorIds) {
				queryString2 += "person_id=" + id + " OR ";
			}
			queryString2 = queryString2.substring(0, queryString2.length()-4);
			PreparedStatement query2 = connection.prepareStatement(queryString2);
			ResultSet result2 = query2.executeQuery();
			while(result2.next()) {
				results.add(result2.getInt("media_id"));
			}
		}
		return results;
	}
	
	public ArrayList<Integer> searchByCategory(String searchString) throws SQLException {
		ArrayList<Integer> categoryIds = new ArrayList<Integer>();
		ArrayList<Integer> results = new ArrayList<Integer>();
		String queryString = "SELECT * FROM category WHERE name LIKE '%" + searchString + "%'";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			categoryIds.add(result.getInt("id"));
		}
		if(categoryIds.size() > 0) {
			String queryString2 = "SELECT media_id FROM media_category WHERE ";
			for(Integer id: categoryIds) {
				queryString2 += "category_id=" + id + " OR ";
			}
			queryString2 = queryString2.substring(0, queryString2.length()-4);
			PreparedStatement query2 = connection.prepareStatement(queryString2);
			ResultSet result2 = query2.executeQuery();
			while(result2.next()) {
				results.add(result2.getInt("media_id"));
			}
		}
		return results;
	}
	
	public ArrayList<Integer> searchByDescription(String searchString) throws SQLException {
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		String queryString = "SELECT * FROM media WHERE MATCH(description, title) AGAINST (?)";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, searchString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			results.add(result.getInt("id"));
		}
		query.close();
		
		return results;
	}
	
	public MediaData getMedia(Integer mediaId) throws SQLException {
		String queryString = "SELECT * FROM media WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			MediaData data = new MediaData();
			data.actors = getMediaPersonList(mediaId, "actor");
			data.authors = getMediaPersonList(mediaId, "author");
			data.producers = getMediaPersonList(mediaId, "producer");
			data.categories = getMediaCategoryList(mediaId);
			data.barcode = result.getString("barcode");
			data.callNumber = result.getString("call_number");
			data.copyright = result.getString("copyright");
			data.description = result.getString("description");
			data.edition = result.getString("edition");
			data.format = result.getInt("format_id") -1;
			data.genre = result.getInt("genre_id") -1;
			data.type = result.getInt("type_id") -1;
			data.isbn = result.getString("isbn");
			data.id = result.getInt("id");
			data.length = result.getString("length");
			data.title = result.getString("title");
			data.library = result.getInt("library_id");
			return data;
		}
		else {
			throw new SQLException();
		}
	}
	
	private ArrayList<String> getMediaPersonList(Integer mediaId, String table) throws SQLException {
		ArrayList<String> personList = new ArrayList<String>();
		String queryString = "SELECT person.name FROM person LEFT JOIN media_" + table + " ON person.id = media_" + table + ".person_id WHERE media_" + table + ".media_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			personList.add(result.getString("person.name"));
		}
		return personList;
	}
	
	private ArrayList<String> getMediaCategoryList(Integer mediaId) throws SQLException {
		ArrayList<String> categoryList = new ArrayList<String>();
		String queryString = "SELECT category.name FROM category LEFT JOIN media_category ON category.id = media_category.category_id WHERE media_category.media_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			categoryList.add(result.getString("category.name"));
		}
		return categoryList;
	}
	
	public MediaData getMedia(String barcode) throws SQLException {
		Integer mediaId = getMediaId(barcode);
		return getMedia(mediaId);
	}
	
	public Integer getMediaId(String barcode) throws SQLException {
		String queryString = "SELECT id FROM media WHERE barcode=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, barcode);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			return result.getInt("id");
		}
		else {
			throw new SQLException();
		}
	}

	public Integer insertMedia(MediaData descriptor, Integer libraryId) throws SQLException {
		String queryString = "INSERT INTO media(type_id, format_id, genre_id, library_id, title, length, call_number, barcode, copyright, description, isbn, edition) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement query = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
		query.setInt(1, descriptor.type+1);
		query.setInt(2,	descriptor.format+1);
		query.setInt(3, descriptor.genre+1);
		query.setInt(4, libraryId);
		query.setString(5, descriptor.title);
		query.setString(6, descriptor.length);
		query.setString(7, descriptor.callNumber);
		query.setString(8, descriptor.barcode);
		query.setString(9, descriptor.copyright);
		query.setString(10, descriptor.description);
		query.setString(11, descriptor.isbn);
		query.setString(12, descriptor.edition);
		query.executeUpdate();
		return HelperModel.getLastInsertId(query);
	}
	
	public void updateMedia(Integer mediaId, MediaData descriptor, Integer libraryId) throws SQLException {
		String queryString = "UPDATE media SET type_id=?, format_id=?, genre_id=?, library_id=?, title=?, length=?, call_number=?, barcode=?, copyright=?, description=?, isbn=?, edition=? WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, descriptor.type+1);
		query.setInt(2,	descriptor.format+1);
		query.setInt(3, descriptor.genre+1);
		query.setInt(4, libraryId);
		query.setString(5, descriptor.title);
		query.setString(6, descriptor.length);
		query.setString(7, descriptor.callNumber);
		query.setString(8, descriptor.barcode);
		query.setString(9, descriptor.copyright);
		query.setString(10, descriptor.description);
		query.setString(11, descriptor.isbn);
		query.setString(12, descriptor.edition);
		query.setInt(13, mediaId);
		query.executeUpdate();
	}
	
	public void deleteMedia(Integer mediaId) throws SQLException {
		String queryString = "DELETE FROM media WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		query.executeUpdate();
	}
	
	public static void main(String[] args) throws SQLException {
		ConnectionData cData = new ConnectionData();
		cData.dbname = "bpa2010";
		cData.driver = "mysql";
		cData.host = "devmonger.com";
		cData.pass = "1q2w3e4r5t";
		cData.port = "3306";
		cData.user = "bpa2010-general";
		MediaModel model = new MediaModel(cData);
		
		MediaData data = model.getMedia("0101010101010");
		Core.debug(data.toString() + " " + data.id.toString());
		model.deleteMedia(data.id);
	}
}
