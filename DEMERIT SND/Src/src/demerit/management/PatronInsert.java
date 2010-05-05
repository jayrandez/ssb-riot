package demerit.management;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

import demerit.AddressData;
import demerit.Controller;
import demerit.Core;
import demerit.JAutoCompleteField;
import demerit.PatronData;
import demerit.Validation;
import demerit.View;
import demerit.models.AddressModel;
import demerit.models.PatronModel;

/**
 * Controller used to insert a patron into the database.
 */
public class PatronInsert extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _side;
	private PatronModel patronModel;
	private AddressModel addressModel;
	
	public PatronInsert(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		HashMap<String, EventListener> handlers = new HashMap<String, EventListener>();
		
		handlers.put("btnReset", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetFields();
			}
		});
		
		handlers.put("btnAddPatron", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = addRoutine();
				if(message == null) {
					resetFields();
					fillAutoCompletes();
					JOptionPane.showMessageDialog(null, "Account added successfully.");
				}
				else {
					JOptionPane.showMessageDialog(null, message);
				}
			}
		});
		
		handlers.put("btnGenerateBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((JTextField)_side.find("txtBarcode")).setText(generateBarcode());
			}
		});

		_actions = view.defineRegion( View.ACTIONS, "Modules/Management/PatronInsert/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/PatronInsert/body.xml",  handlers);
		_default = view.defineRegion( View.DEFAULT, "Modules/Management/PatronInsert/default.xml", handlers);
		_side = view.defineRegion(    View.SIDE,    "Modules/Management/PatronInsert/side.xml",    handlers);
		
		makeModels();
		fillAutoCompletes();
		fillComboBoxes();
		
		return view;
	}
	
	private void makeModels() {
		patronModel = (PatronModel)core.createModel("PatronModel");
		addressModel = (AddressModel)core.createModel("AddressModel");
	}
	
	private void fillComboBoxes() {
		String[] states = {
			"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL",
			"IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT",
			"NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI",
			"SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "DC",
			"FM", "GU", "MH", "MP", "PW", "PR", "VI", "AE", "AA", "AE", "AE", "AE", "AP"
		};
		
		JComboBox combo = ((JComboBox)_body.find("cbbState"));
		
		for(int i = 0; i < states.length; i++) {
			combo.addItem(states[i]);
		}
		
	}
	
	private void fillAutoCompletes() {
		try {
			ArrayList<AddressData> addresses = addressModel.listAddresses();
			ArrayList<String> streets = new ArrayList<String>();
			ArrayList<String> cities = new ArrayList<String>();
			ArrayList<String> zips = new ArrayList<String>();
			for(AddressData address: addresses) {
				if(!streets.contains(address.street))
					streets.add(address.street);
				if(!cities.contains(address.city))
					cities.add(address.city);
				if(!zips.contains(address.zip))
					zips.add(address.zip);
			}
			((JAutoCompleteField)_body.find("autStreet")).setPossibilities(streets);
			((JAutoCompleteField)_body.find("autCity")).setPossibilities(cities);
			((JAutoCompleteField)_body.find("autZip")).setPossibilities(zips);
		}
		catch (SQLException e) {
			Core.debug("Couldn't populate possible addresses.");
			e.printStackTrace();
		}
		
	}
	
	private void resetFields() {
		((JTextField)_body.find("txtFirstName")).setText("");
		((JTextField)_body.find("txtLastName")).setText("");
		((JTextField)_body.find("autStreet")).setText("");
		((JTextField)_body.find("autCity")).setText("");
		((JTextField)_body.find("autZip")).setText("");
		((JTextField)_body.find("txtPhone")).setText("");
		((JTextField)_body.find("txtEmail")).setText("");
		((JTextField)_side.find("txtBarcode")).setText("");
	}
	
	private String addRoutine() {
		String firstName = ((JTextField)_body.find("txtFirstName")).getText();
		String lastName = ((JTextField)_body.find("txtLastName")).getText();
		JComboBox combo = ((JComboBox)_body.find("cbbState"));
		
		try {
			AddressData addressData = new AddressData();
			addressData.city = ((JTextField)_body.find("autCity")).getText();
			addressData.state = ((String)combo.getSelectedItem());
			addressData.street = ((JTextField)_body.find("autStreet")).getText();
			addressData.zip = ((JTextField)_body.find("autZip")).getText();
			
			String error = null;
			error = Validation.validateFilled(addressData.street);
			if(error != null)
				return "Street: " + error;
			error = Validation.validateFilled(addressData.city);
			if(error != null)
				return "City: " + error;
			error = Validation.validateFilled(addressData.state);
			if(error != null)
				return "State: " + error;
			error = Validation.validateZip(addressData.zip);
			if(error != null)
				return "Zip: " + error;
			
			PatronData patronData = new PatronData();
			patronData.barcode = ((JTextField)_side.find("txtBarcode")).getText();
			patronData.birthday = new Date();
			patronData.email = ((JTextField)_body.find("txtEmail")).getText();
			patronData.name = firstName + " " + lastName;
			patronData.phone = ((JTextField)_body.find("txtPhone")).getText();
			
			error = Validation.validateFilled(patronData.barcode);
			if(error != null)
				return "Barcode: " + error;
			error = Validation.validateFilled(patronData.name);
			if(error != null)
				return "Name: " + error;
			error = Validation.validatePhone(patronData.phone);
			if(error != null)
				return "Phone: " + error;
			error = Validation.validateFilled(patronData.phone);
			if(error != null)
				return "Phone: " + error;
			
			Integer addressId = addressModel.insertAddress(addressData);
			patronData.addressId = addressId;
			patronModel.insertPatron(patronData);
			
			
			return null;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			Core.debug("Couldn't add patron to database.");
			return "There was a problem accessing the database.";
		}
	}

	
	private String generateBarcode() {
		String[] array = new String[14];
		int r = 0;
		int sum = 0;
		Random random = new Random();
		String barcode = "";

		//get the entity code
		array[0] = "2";
		
		//enter library code
		array[1] = "1";
		array[2] = "3";
		array[3] = "3";
		array[4] = "7";
		
		//get random number
		for (int x = 5; x <= 12; x++){
			r = random.nextInt(9);
			array[x] = (new Integer(r).toString());
		}
		
		//odd add and mult 3, even just add
		for (int x = 0; x<= 12; x++){
			if ((x+1)==0){
				sum += Integer.parseInt(array[x])*3;
			}
			else if(x%2==0){
				sum += Integer.parseInt(array[x]);
			}
			else {
				sum += Integer.parseInt(array[x])*3;
			}
		}
		
		//get check sum number
		int y = 10-(sum % 10);
		
		array[13] = (new Integer(y).toString());
		
		for (int x = 0; x <= 12; x++){
			barcode += array[x];
		}
		
		return barcode;
	}
}
