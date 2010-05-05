package demerit.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


import demerit.ConnectionData;
import demerit.Core;
import demerit.MediaData;
import demerit.Model;
import demerit.TransactionData;

/**
 * A helper model which performs various SQL tasks to extract repetitive code.
 */
public class HelperModel extends Model {
	
	public HelperModel(ConnectionData connectionData) {
		super(connectionData);
	}

	public static Integer getLastInsertId(Statement stmt) throws SQLException{
		ResultSet rs = null;
		int id = 0;
		rs = stmt.getGeneratedKeys();
		if(rs.next()) {
	        id = rs.getInt(1);
	    }
		else {
			rs.close();
	        throw new SQLException("Couldn't get last Id");
	    }
		rs.close();
		return id;
	}
	
	public static java.util.Date toJavaDate(java.sql.Timestamp timestamp) {
	    long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
	    return new java.util.Date(milliseconds);
	}
	
	public static java.sql.Date toSQLDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
	
	public static java.util.Date futureDate(Connection connection, Integer days) throws SQLException {
		String queryString = "SELECT DATE_ADD(NOW(), INTERVAL " + days + " DAY)";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			java.sql.Timestamp timestamp = result.getTimestamp(1);
			query.close();
			return toJavaDate(timestamp);
		}
		else {
			query.close();
			throw new SQLException();
		}
	}
	
	public static Double mediaDues(TransactionModel transactionModel, Integer mediaId) throws SQLException {
		Double fees = 0.0;
		TransactionData transaction = transactionModel.getLastTransaction(mediaId);
		Date due = transaction.due;
		Integer days = new Date().compareTo(due);
		if(days > 0) {
			long diff = new Date().getTime() - due.getTime();
			days = (int)diff / (1000 * 60 * 60 * 24);
			fees = (Core.dailyCharge * (double)days);
		}
		return fees;
	}
	
	public static Double calculateDues(TransactionModel transactionModel, ArrayList<Integer> checkedOutIds) throws SQLException {
		Double fees = 0.0;
		for(Integer mediaId: checkedOutIds) {
			fees += mediaDues(transactionModel, mediaId);
		}
		return fees;
	}
	
	public static Integer getOverdueCount(TransactionModel transactionModel, ArrayList<Integer> checkedOutIds) throws SQLException {
		Integer overdue = 0;
		for(Integer mediaId: checkedOutIds) {
			TransactionData transaction = transactionModel.getLastTransaction(mediaId);
			Date due = transaction.due;
			Integer days = new Date().compareTo(due);
			if(days > 0) {
				overdue++;
			}
		}
		return overdue;
	}
}
