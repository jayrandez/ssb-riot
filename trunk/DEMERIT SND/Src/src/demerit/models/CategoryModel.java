package demerit.models;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import demerit.*;

/**
 * Model used to connect to the category table and assign categories to media.
 */
public class CategoryModel extends Model {
	

	public CategoryModel(ConnectionData connectionData) {
		super(connectionData);
	}

	public ArrayList<String> listCategories() throws SQLException {
		ArrayList<String> results = new ArrayList<String>();
		String queryString = "SELECT * FROM category";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			results.add(result.getString("name"));
		}
		return results;
	}
	
	public void categoryToMedia(String category, Integer mediaId) throws SQLException {
		Integer categoryId = getCategory(category);
		
		if(categoryId == null) {
			String queryString = "INSERT INTO category(name) VALUES(?)";
			PreparedStatement query = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
			query.setString(1, category);
			query.executeUpdate();
			categoryId = HelperModel.getLastInsertId(query);
			query.close();
		}
			
		String queryString2 = "INSERT INTO media_category(media_id, category_id) VALUES(?, ?)";
		PreparedStatement query2 = connection.prepareStatement(queryString2);
		query2.setInt(1, mediaId);
		query2.setInt(2, categoryId);
		query2.executeUpdate();
		query2.close();
	}
	
	public void removeMediaCategories(Integer mediaId) throws SQLException {
		String queryString = "DELETE FROM media_category WHERE media_id = ?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		query.executeUpdate();
	}
	
	public Integer getCategory(String category) throws SQLException {
		String queryString = "SELECT * FROM category WHERE name=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, category);
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
		CategoryModel model = new CategoryModel(cData);
		
		Core.debug(model.listCategories().toString());
	}
}
