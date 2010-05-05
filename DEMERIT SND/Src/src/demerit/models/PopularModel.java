package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import demerit.ConnectionData;
import demerit.Core;
import demerit.MediaData;
import demerit.Model;
import demerit.PatronData;
import demerit.TransactionData;

/**
 * Model used to generate the report for the Popular model.
 */
public class PopularModel extends Model {
	
	private PatronModel patronModel;
	private MediaModel mediaModel;
	private TransactionModel transactionModel;
	
	public PopularModel(ConnectionData connectionData) {
		super(connectionData);
		patronModel = new PatronModel(connectionData);
		mediaModel = new MediaModel(connectionData);
		transactionModel = new TransactionModel(connectionData);
	}
	
	public ArrayList<Integer> getPopularItems() throws SQLException {
		String queryString = "SELECT * FROM media ORDER BY checkouts DESC LIMIT 10";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		while(result.next()) {
			ids.add(result.getInt("id"));
		}
		return ids;
	}
}