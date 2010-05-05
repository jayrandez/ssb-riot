package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import demerit.ConnectionData;
import demerit.Core;
import demerit.Model;
import demerit.TransactionData;

/**
 * Important model used to connect to the transaction and checkout tables
 * and perform various transactions involving media and patrons.
 */
public class TransactionModel extends Model {

	public TransactionModel(ConnectionData connectionData) {
		super(connectionData);
	}
	
	public Boolean isCheckedOut(Integer mediaId) throws SQLException {
		String queryString = "SELECT * FROM checkout WHERE media_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		if(result.next())
			return true;
		else
			return false;
	}
	
	public Integer timesCheckedOut(Integer mediaId) throws SQLException {
		String queryString = "SELECT checkouts FROM media WHERE id = ?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		
		if(result.next()) {
			return result.getInt("checkouts");
		}
		else {
			return 0;
		}
	}
	
	public void checkOutMedia(Integer mediaId, Integer patronId, Integer days, Integer library) throws SQLException {
		String queryString = "INSERT INTO checkout(media_id, patron_id) VALUES(?, ?)";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		query.setInt(2, patronId);
		query.executeUpdate();
		query.close();
		
		java.sql.Date dueDate = HelperModel.toSQLDate(HelperModel.futureDate(connection, days));
		
		String queryString2 = "INSERT INTO transaction(media_id, patron_id, library_id, type, checkout_date, transaction_date, due_date) VALUES(?, ?, ?, ?, NOW(), NOW(), ?)";
		PreparedStatement query2 = connection.prepareStatement(queryString2);
		query2.setInt(1, mediaId);
		query2.setInt(2, patronId);
		query2.setInt(3, library);
		query2.setString(4, "out");
		query2.setDate(5, dueDate);
		query2.executeUpdate();
		
		Integer checkouts = timesCheckedOut(mediaId) + 1;
		String queryString3 = "UPDATE media SET checkouts=? WHERE id=?";
		PreparedStatement query3 = connection.prepareStatement(queryString3);
		query3.setInt(1, checkouts);
		query3.setInt(2, mediaId);
		query3.executeUpdate();
	}
	
	public void renewMedia(Integer mediaId, Integer days, Integer library) throws SQLException {
		TransactionData last = getLastTransaction(mediaId);
		if(last == null) {
			throw new SQLException();
		}
		
		java.sql.Date checkout = HelperModel.toSQLDate(last.checkout);
		java.sql.Date due = HelperModel.toSQLDate(HelperModel.futureDate(connection, days));
		
		String queryString = "INSERT INTO transaction(media_id, patron_id, library_id, type, checkout_date, transaction_date, due_date) VALUES(?, ?, ?, ?, ?, NOW(), ?)";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		query.setInt(2, last.patronId);
		query.setInt(3, library);
		query.setString(4, "renew");
		query.setDate(5, checkout);
		query.setDate(6, due);
		query.executeUpdate();
	}
	
	public void checkInMedia(Integer mediaId, Integer library) throws SQLException {
		Double fineOnMedia = HelperModel.mediaDues(this, mediaId);
		
		String queryString = "DELETE FROM checkout WHERE media_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		query.executeUpdate();
		
		TransactionData last = getLastTransaction(mediaId);
		if(last == null) {
			throw new SQLException();
		}
		
		java.sql.Date checkout = HelperModel.toSQLDate(last.checkout);
		java.sql.Date due = HelperModel.toSQLDate(last.due);
		
		String queryString2 = "INSERT INTO transaction(media_id, patron_id, library_id, type, checkout_date, transaction_date, due_date) VALUES(?, ?, ?, ?, ?, NOW(), ?)";
		PreparedStatement query2 = connection.prepareStatement(queryString2);
		query2.setInt(1, mediaId);
		query2.setInt(2, last.patronId);
		query2.setInt(3, library);
		query2.setString(4, "in");
		query2.setDate(5, checkout);
		query2.setDate(6, due);
		query2.executeUpdate();
		
		String queryString3 = "SELECT dues FROM patron WHERE id=?";
		PreparedStatement query3 = connection.prepareStatement(queryString3);
		query3.setInt(1, last.patronId);
		ResultSet result3 = query3.executeQuery();
		if(result3.next()) {
			fineOnMedia += result3.getDouble("dues");
		}
		else {
			throw new SQLException();
		}
		
		String queryString4 = "UPDATE patron SET dues=? WHERE id=?";
		PreparedStatement query4 = connection.prepareStatement(queryString4);
		query4.setDouble(1, fineOnMedia);
		query4.setInt(2, last.patronId);
		query4.executeUpdate();
	}
	
	public TransactionData getLastTransaction(Integer mediaId) throws SQLException {
		String queryString = "SELECT * FROM transaction WHERE media_id=? ORDER BY transaction_date DESC LIMIT 1";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, mediaId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			TransactionData data = new TransactionData();
			data.patronId = result.getInt("patron_id");
			data.mediaId = result.getInt("media_id");
			data.libraryId = result.getInt("library_id");
			data.type = result.getString("type");
			data.checkout = HelperModel.toJavaDate(result.getTimestamp("checkout_date"));
			data.transaction = HelperModel.toJavaDate(result.getTimestamp("transaction_date"));
			data.due = HelperModel.toJavaDate(result.getTimestamp("due_date"));
			return data;
		}
		else {
			return null;
		}
	}
	
	public ArrayList<Integer> getPatronBooks(Integer patronId) throws SQLException {
		ArrayList<Integer> results = new ArrayList<Integer>();
		String queryString = "SELECT * FROM checkout WHERE patron_id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, patronId);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			results.add(result.getInt("media_id"));
		}
		return results;
	}
	
	public static void main(String[] args) throws SQLException {
		ConnectionData cData = new ConnectionData();
		cData.dbname = "bpa2010";
		cData.driver = "mysql";
		cData.host = "devmonger.com";
		cData.pass = "1q2w3e4r5t";
		cData.port = "3306";
		cData.user = "bpa2010-general";
		TransactionModel model = new TransactionModel(cData);
		
		model.checkInMedia(74, 1);
	}
}
