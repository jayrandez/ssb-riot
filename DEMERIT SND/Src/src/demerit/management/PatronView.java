package demerit.management;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.swixml.SwingEngine;
import org.swixml.XVBox;

import demerit.AddressData;
import demerit.Controller;
import demerit.Core;
import demerit.JAutoCompleteField;
import demerit.JAutoCompleteScroll;
import demerit.JLinkLabel;
import demerit.JMediaTable;
import demerit.MediaData;
import demerit.PatronData;
import demerit.Validation;
import demerit.View;
import demerit.models.AddressModel;
import demerit.models.CategoryModel;
import demerit.models.HelperModel;
import demerit.models.MediaModel;
import demerit.models.PatronModel;
import demerit.models.PersonModel;
import demerit.models.TransactionModel;

/**
 * Controller used to view information about a patron.
 */
public class PatronView extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _mode;
	private SwingEngine _default;
	PatronModel patronModel;
	AddressModel addressModel;
	TransactionModel transactionModel;
	MediaModel mediaModel;
	HashMap<String, EventListener> handlers;
	
	PatronData current;
	AddressData currentAddress;
	
	public PatronView(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		
		handlers = new HashMap<String, EventListener>();
		
		handlers.put("txtBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String barcode = ((JTextField)_mode.find("txtBarcode")).getText();
				if(!selectPatron(barcode)) {
					JOptionPane.showMessageDialog(null, "Couldn't find the account in the database.");
				}
			}
		});
		handlers.put("btnUpdate", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = updatePatron();
				if(message != null) {
					JOptionPane.showMessageDialog(null, message);
				}
			}
		});
		handlers.put("btnRemove", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removePatron();
			}
		});
		handlers.put("btnCheckIn", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkInSelected();
				selectPatron(current.barcode);
			}
		});
		handlers.put("btnPaid", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				duesPaid();
				selectPatron(current.barcode);
			}
		});
		handlers.put("tblMedia", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "displaying");
			}
		});

		_actions = view.defineRegion( View.ACTIONS, "Modules/Management/PatronView/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/PatronView/body.xml",  handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Management/PatronView/mode.xml",      handlers);
		_default = view.defineRegion(View.DEFAULT, "Modules/Management/PatronView/default.xml", handlers);
		
		clearForm();
		makeModels();
		
		if(parameters.size() > 0) {
			String barcode = (String) parameters.get(0);
			selectPatron(barcode);
		}
		
		return view;
	}
	
	private void fillTable() {
		try {
			JMediaTable table = ((JMediaTable)_body.find("tblMedia"));
			Double originalDues = patronModel.getPatronDues(current.id);
			ArrayList<Integer> bookIds = transactionModel.getPatronBooks(current.id);
			Double totalDues = originalDues + HelperModel.calculateDues(transactionModel, bookIds);
			for(Integer bookId: bookIds) {
				MediaData descriptor = mediaModel.getMedia(bookId);
				if(HelperModel.mediaDues(transactionModel, bookId) > 0) {
					descriptor.status = "Overdue";
				}
				else {
					descriptor.status = "Out";
				}
				descriptor.selected = true;
				table.addItem(descriptor);
			}
			DecimalFormat formatter = new DecimalFormat("$0.00");
			JLabel label = (JLabel)_body.find("lblDues");
			label.setText(formatter.format(totalDues));
			if(HelperModel.getOverdueCount(transactionModel, bookIds) == 0) {
				_default.find("btnPaid").setEnabled(true);
			}
			else {
				_default.find("btnPaid").setEnabled(false);
			}
		}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, "There was a problem getting information about the checked out books.");
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
	
	protected void checkInSelected() {
		JMediaTable table = ((JMediaTable)_body.find("tblMedia"));
		try {
			ArrayList<MediaData> returning = table.removeSelected();
			for(MediaData item: returning) {
				transactionModel.checkInMedia(item.id, core.getLibraryID());
			}
		}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, "There was a problem checking in the books.");
		}
	}

	protected void duesPaid() {
		try {
			patronModel.setPatronDues(current.id, 0.0);
		}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, "There was a problem connecting to the database.");
		}
	}

	private void prepareFields() {
		XVBox parent = (XVBox)((JPanel)_body.find("vbxPatronView")).getComponent(0);
		for(int i = 1; i < parent.getComponentCount(); i++) {
			JPanel row = (JPanel)parent.getComponent(i);
			JLabel label = (JLabel)row.getComponent(1);
			JComponent editor = (JComponent)row.getComponent(2);
			JLinkLabel link = (JLinkLabel)row.getComponent(3);
			link.addActionListener(new FieldLinkListener(editor, label, link));
		}
		JMediaTable table = (JMediaTable)_body.find("tblMedia");
		ArrayList<Integer> fields = new ArrayList<Integer>();
		fields.add(JMediaTable.SELECTED);
		fields.add(JMediaTable.FORMAT);
		fields.add(JMediaTable.TITLE);
		fields.add(JMediaTable.STATUS);
		table.displayFields(fields);
		table.setFont(new Font("Arial", 0, 16));
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
	
	private class FieldLinkListener implements ActionListener {
		JComponent editor;
		JLabel label;
		JLinkLabel link;
		public FieldLinkListener(JComponent editor, JLabel label, JLinkLabel link) {
			this.editor = editor;
			this.label = label;
			this.link = link;
		}
		public void actionPerformed(ActionEvent e) {
			editor.setVisible(true);
			label.setVisible(false);
			link.setVisible(false);
		}
	}

	protected void removePatron() {
		try {
			patronModel.deletePatron(current.id);
			clearForm();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "There was a problem connecting to the database.");
		}
	}

	private void clearForm() {
		view.undefineRegion(View.BODY);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/PatronView/body.xml",  handlers);
		prepareFields();
		fillComboBoxes();
		((JPanel)_body.find("vbxPatronView")).setVisible(false);
		//((JMediaTable)_body.find("tblMedia")).setVisible(false);
		((JTextField)_mode.find("txtBarcode")).setText("");
		((JTextField)_mode.find("txtBarcode")).requestFocus();
	}
	
	private Object getRowValue(JPanel row) {
		if(row.getComponent(2) instanceof JAutoCompleteScroll) {
			JAutoCompleteScroll editor = ((JAutoCompleteScroll)row.getComponent(2));
			return editor.getValues();
		}
		else if(row.getComponent(2) instanceof JComboBox) {
			JComboBox editor = ((JComboBox)row.getComponent(2));
			return editor.getSelectedItem();
		}
		else if(row.getComponent(2) instanceof JTextComponent) {
			JTextComponent editor = ((JTextComponent)row.getComponent(2));
			return editor.getText();
		}
		return null;
	}
	
	private void setRowValue(JPanel row, Object value) {
		JLabel label = ((JLabel)row.getComponent(1));
		String labelText = "";
		if(row.getComponent(2) instanceof JAutoCompleteScroll) {
			JAutoCompleteScroll editor = ((JAutoCompleteScroll)row.getComponent(2));
			ArrayList<String> values = (ArrayList<String>)value;
			for(String item: values) {
				labelText += ", " + item;
				editor.addRow(item);
			}
			if(labelText.length() > 2)
				labelText = labelText.substring(2);
		}
		else if(row.getComponent(2) instanceof JComboBox) {
			JComboBox editor = ((JComboBox)row.getComponent(2));
			labelText = (String)value;
			for(int i = 0; i < editor.getItemCount(); i++) {
				if(editor.getItemAt(i).equals((String)value)) {
					editor.setSelectedIndex(i);
					break;
				}
			}
		}
		else if(row.getComponent(2) instanceof JTextComponent) {
			JTextComponent editor = ((JTextComponent)row.getComponent(2));
			labelText = (String)value;
			editor.setText((String)value);
		}
		if(labelText.length() > 50) {
			labelText = labelText.substring(0, 49) + " . . .";
		}
		label.setText(labelText);
	}

	protected String updatePatron() {
		try {
			PatronData updated = new PatronData();
			AddressData updatedAddress = new AddressData();
			updated.name = current.name;
			updated.id = current.id;
			updated.birthday = current.birthday;
			
			JPanel a;
			a = (JPanel)_body.find("pnlPhone");
			updated.phone = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlEmail");
			updated.email = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlBarcode");
			updated.barcode = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlCity");
			updatedAddress.city = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlStreet");
			updatedAddress.street = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlZip");
			updatedAddress.zip = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlState");
			updatedAddress.state = (String)getRowValue(a);
			
			String error = null;
			error = Validation.validateFilled(updatedAddress.street);
			if(error != null)
				return "Street: " + error;
			error = Validation.validateFilled(updatedAddress.city);
			if(error != null)
				return "City: " + error;
			error = Validation.validateFilled(updatedAddress.state);
			if(error != null)
				return "State: " + error;
			error = Validation.validateZip(updatedAddress.zip);
			if(error != null)
				return "Zip: " + error;
			error = Validation.validateFilled(updated.barcode);
			if(error != null)
				return "Barcode: " + error;
			error = Validation.validateFilled(updated.name);
			if(error != null)
				return "Name: " + error;
			error = Validation.validatePhone(updated.phone);
			if(error != null)
				return "Phone: " + error;
			error = Validation.validateFilled(updated.phone);
			if(error != null)
				return "Phone: " + error;

			updated.addressId = addressModel.insertAddress(updatedAddress);
			patronModel.updatePatron(updated.id, updated);
			
			clearForm();
			selectPatron(updated.barcode);
			return null;
		}
		catch(SQLException ex) {
			return "There was a problem connecting to the database.";
		}
	}

	protected boolean selectPatron(String barcode) {
		try {
			clearForm();
			current = patronModel.getPatron(barcode);
			currentAddress = addressModel.getAddress(current.addressId);
			
			((JLabel)_body.find("lblName")).setText(current.name);
			
			JPanel a;
			a = (JPanel)_body.find("pnlPhone");
			setRowValue(a, current.phone);
			a = (JPanel)_body.find("pnlEmail");
			setRowValue(a, current.email);
			a = (JPanel)_body.find("pnlBarcode");
			setRowValue(a, current.barcode);
			a = (JPanel)_body.find("pnlCity");
			setRowValue(a, currentAddress.city);
			a = (JPanel)_body.find("pnlStreet");
			setRowValue(a, currentAddress.street);
			a = (JPanel)_body.find("pnlZip");
			setRowValue(a, currentAddress.zip);
			a = (JPanel)_body.find("pnlState");
			setRowValue(a, currentAddress.state);
			
			
			fillTable();
			fillAutoCompletes();
			
			((JPanel)_body.find("vbxPatronView")).setVisible(true);
			//((JMediaTable)_body.find("tblMedia")).setVisible(true);
			((JTextField)_mode.find("txtBarcode")).setText("");
			((JTextField)_mode.find("txtBarcode")).requestFocus();
			return true;
		}
		catch(SQLException ex) {
			clearForm();
			Core.debug("Couldn't retrieve media with that barcode");
			return false;
		}
		
	}

	private void makeModels() {
		patronModel = (PatronModel)core.createModel("PatronModel");
		addressModel = (AddressModel)core.createModel("AddressModel");
		transactionModel = (TransactionModel)core.createModel("TransactionModel");
		mediaModel = (MediaModel)core.createModel("MediaModel");
	}
	
	
}
