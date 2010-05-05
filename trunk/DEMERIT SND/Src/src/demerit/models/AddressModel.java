package demerit.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import demerit.AddressData;
import demerit.ConnectionData;
import demerit.Core;
import demerit.Model;

/**
 * The model used to connect mostly to manipulate/access the address table.
 */
public class AddressModel extends Model {

	public AddressModel(ConnectionData connectionData) {
		super(connectionData);
	}

	public ArrayList<AddressData> listAddresses() throws SQLException {
		ArrayList<AddressData> results = new ArrayList<AddressData>();
		String queryString = "SELECT * FROM address";
		PreparedStatement query = connection.prepareStatement(queryString);
		ResultSet result = query.executeQuery();
		while(result.next()) {
			AddressData data = new AddressData();
			data.city = result.getString("city");
			data.state = result.getString("state");
			data.street = result.getString("street");
			data.zip = result.getString("zip");
			results.add(data);
		}
		return results;
	}
	
	public Integer insertAddress(AddressData data) throws SQLException {
		Integer addressId = getAddress(data);
		if(addressId == null) {
			String queryString = "INSERT INTO address(street, city, state, zip) VALUES(?, ?, ?, ?)";
			PreparedStatement query = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
			query.setString(1, data.street);
			query.setString(2, data.city);
			query.setString(3, data.state);
			query.setString(4, data.zip);
			query.executeUpdate();
			addressId = HelperModel.getLastInsertId(query);
			query.close();
		}
		return addressId;
	}
	
	public AddressData getAddress(Integer addressId) throws SQLException {
		String queryString = "SELECT * FROM address WHERE id=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setInt(1, addressId);
		ResultSet result = query.executeQuery();
		
		if(result.next()) {
			AddressData send = new AddressData();
			send.city = result.getString("city");
			send.state = result.getString("state");
			send.street = result.getString("street");
			send.zip = result.getString("zip");
			query.close();
			return send;
		}
		else {
			query.close();
			throw new SQLException();
		}
	}
	
	public Integer getAddress(AddressData data) throws SQLException {
		String queryString = "SELECT * FROM address WHERE street=? AND city=? AND zip=? AND state=?";
		PreparedStatement query = connection.prepareStatement(queryString);
		query.setString(1, data.street);
		query.setString(2, data.city);
		query.setString(3, data.zip);
		query.setString(4, data.state);
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
		AddressModel model = new AddressModel(cData);
		
		AddressData data = new AddressData();
		data.city = "Crown Point";
		data.state = "IN";
		data.street = "4898 W. 84th Terr.";
		data.zip = "46307";
		Core.debug(model.getAddress(data).toString());
	}
	
}
