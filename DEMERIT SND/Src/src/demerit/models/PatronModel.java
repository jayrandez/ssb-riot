package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import demerit.AddressData;
import demerit.ConnectionData;
import demerit.Core;
import demerit.MediaData;
import demerit.Model;
import demerit.PatronData;

/**
 * Important model which obtains information about the patron.
 */
public class PatronModel extends Model {
	
	public PatronModel(ConnectionData connectionData) {
		super(connectionData);
	}
	
	public ArrayList<Integer> listPatronIds() throws SQLException {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		String queryString = "SELECT id FROM patron";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			ids.add(result.getInt("id"));
		}
		return ids;
	}
	
	public PatronData getPatron(Integer patronId) throws SQLException {
		String queryString = "SELECT * FROM patron WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, patronId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			PatronData data = new PatronData();
			data.addressId = result.getInt("address_id");
			data.barcode = result.getString("barcode");
			data.birthday = HelperModel.toJavaDate(result.getTimestamp("birthday"));
			data.creation = HelperModel.toJavaDate(result.getTimestamp("creation"));
			data.email = result.getString("email");
			data.id = result.getInt("id");
			data.name = result.getString("name");
			data.phone = result.getString("phone");
			return data;
		}
		else {
			throw new SQLException();
		}
	}
	
	public Double getPatronDues(Integer patronId) throws SQLException {
		String queryString = "SELECT dues FROM patron WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, patronId);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			return result.getDouble("dues");
		}
		else {
			throw new SQLException();
		}
	}
	
	public void setPatronDues(Integer patronId, Double dues) throws SQLException {
		String queryString = "UPDATE patron SET dues=? WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setDouble(1, dues);
		query.setInt(2, patronId);
		query.executeUpdate();
	}
	
	public void deletePatron(Integer patronId) throws SQLException {
		String queryString = "DELETE FROM patron WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, patronId);
		query.executeUpdate();
	}
	
	public PatronData getPatron(String barcode) throws SQLException {
		String queryString = "SELECT id FROM patron WHERE barcode=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, barcode);
		ResultSet result = query.executeQuery();
		if(result.next()) {
			return getPatron(result.getInt("id"));
		}
		else {
			throw new SQLException();
		}
	}

	public void insertPatron(PatronData descriptor) throws SQLException {
		String queryString = "INSERT INTO patron(name, phone, email, barcode, birthday, creation, address_id) VALUES(?, ?, ?, ?, ?, NOW(), ?)";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, descriptor.name);
		query.setString(2, descriptor.phone);
		query.setString(3, descriptor.email);
		query.setString(4, descriptor.barcode);
		query.setDate(5, HelperModel.toSQLDate(descriptor.birthday));
		query.setInt(6, descriptor.addressId);
		query.executeUpdate();
		query.close();
	}
	
	public void updatePatron(Integer patronId, PatronData descriptor) throws SQLException {
		String queryString = "UPDATE patron SET name=?, phone=?, email=?, barcode=?, birthday=?, address_id=? WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, descriptor.name);
		query.setString(2, descriptor.phone);
		query.setString(3, descriptor.email);
		query.setString(4, descriptor.barcode);
		query.setDate(5, HelperModel.toSQLDate(descriptor.birthday));
		query.setInt(6, descriptor.addressId);
		query.setInt(7, patronId);
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
		PatronModel model = new PatronModel(cData);
		
		PatronData data = model.getPatron("1234567812347");
		Core.debug(data.toString() + " " + data.id.toString());
		model.deletePatron(data.id);
	}
}
