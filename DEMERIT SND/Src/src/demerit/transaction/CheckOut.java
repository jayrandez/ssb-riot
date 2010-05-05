package demerit.transaction;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

import demerit.Controller;
import demerit.Core;
import demerit.JLinkLabel;
import demerit.JMediaTable;
import demerit.MediaData;
import demerit.PatronData;
import demerit.TransactionData;
import demerit.View;
import demerit.models.HelperModel;
import demerit.models.MediaModel;
import demerit.models.PatronModel;
import demerit.models.TransactionModel;

/**
 * Most often used controller where librarians can check out books for patrons.
 */
public class CheckOut extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _header;
	private SwingEngine _mode;
	private SwingEngine _switch;
	HashMap<String, EventListener> handlers;
	PatronModel patronModel;
	MediaModel mediaModel;
	TransactionModel transactionModel;
	
	PatronData currentPatron;
	
	public CheckOut(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		
		handlers = new HashMap<String, EventListener>();
		
		handlers.put("btnReset", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelected();
			}
		});
		
		handlers.put("btnRenew", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = renewProceure();
				if(message != null) {
					JOptionPane.showMessageDialog(null, message);
				}
				else {
					resetForm();
				}
			}
		});
		
		handlers.put("btnCheckOut", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = checkoutProceure();
				if(message != null) {
					JOptionPane.showMessageDialog(null, message);
				}
				else {
					resetForm();
				}
			}
		});
		
		handlers.put("lnkPatron", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPatronController();
			}
		});
		
		handlers.put("txtPatronID", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectPatron();
			}
		});
		
		handlers.put("lnkChange", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetForm();
			}
		});
		
		handlers.put("txtBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMedia();
			}
		});

		_actions = view.defineRegion( View.ACTIONS, "Modules/Transaction/CheckOut/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Transaction/CheckOut/body.xml",  handlers);
		_default = view.defineRegion( View.DEFAULT, "Modules/Transaction/CheckOut/default.xml", handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Transaction/CheckOut/mode.xml",      handlers);
		_switch = view.defineRegion(View.SWITCH,"Modules/Transaction/CheckOut/switch.xml", handlers);
		
		view.addComponentListener(new java.awt.event.ComponentAdapter() {   
			public void componentShown(java.awt.event.ComponentEvent e) {
				resetForm();
			}
		});
		
		makeModels();
		
		return view;
	}
	
	protected void selectMedia() {
		try {
			JTextField barcode = ((JTextField)_switch.find("txtBarcode"));
			MediaData data = mediaModel.getMedia(barcode.getText());
			data.selected = true;
			((JMediaTable)_body.find("tblMedia")).addItem(data);
			barcode.setText("");
			barcode.requestFocus();
		}
		catch (SQLException e) {
			Core.debug("Add media to table failed.");
			JOptionPane.showMessageDialog(null, "Couldn't find media in the database.");
		}
	}

	protected void selectPatron() {
		try {
			JTextField patronField = ((JTextField)_mode.find("txtPatronID"));
			currentPatron = patronModel.getPatron(patronField.getText());

			ArrayList<Integer> checkedOutIds = transactionModel.getPatronBooks(currentPatron.id);
			Integer out = checkedOutIds.size();
			Integer overdue = HelperModel.getOverdueCount(transactionModel, checkedOutIds);
			Double fees = HelperModel.calculateDues(transactionModel, checkedOutIds);
			
			_header = view.defineRegion(  View.HEADER,  "Modules/Transaction/CheckOut/header.xml",  handlers);
			DecimalFormat format = new DecimalFormat("$0.00");
			((JLinkLabel)_header.find("lnkPatron")).setText(currentPatron.name);
			((JLabel)_header.find("lblStatus")).setText("    " + out + " Checked Out, " + overdue + " Overdue.");
			((JLabel)_header.find("lblDues")).setText(format.format(fees));
			
			patronField.setEnabled(false);
			JTextField barcode = ((JTextField)_switch.find("txtBarcode"));
			barcode.setEnabled(true);
			barcode.requestFocus();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "An account corresponding to that barcode couldn't be found.");
			resetForm();
		}
	}

	protected void showPatronController() {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(currentPatron.barcode);
		core.startController("Management", "PatronView", params, "Account Information");
	}

	protected void resetForm() {
		JTextField patron = ((JTextField)_mode.find("txtPatronID"));
		patron.setEnabled(true);
		patron.setText("");
		JTextField barcode = ((JTextField)_switch.find("txtBarcode"));
		barcode.setEnabled(false);
		barcode.setText("");
		JMediaTable table = ((JMediaTable)_body.find("tblMedia"));
		table.removeAllItems();
		patron.requestFocus();
		view.undefineRegion(View.HEADER);
	}

	protected String checkoutProceure() {
		try {
			ArrayList<MediaData> scanned = ((JMediaTable)_body.find("tblMedia")).getItems();
			for(MediaData data: scanned) {
				transactionModel.checkOutMedia(data.id, currentPatron.id, 14, core.getLibraryID());
			}
			resetForm();
			return null;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			Core.debug("One checkout attempt failed.");
			return "There was a problem connecting to the database.";
		}
	}

	protected String renewProceure() {
		try {
			ArrayList<MediaData> scanned = ((JMediaTable)_body.find("tblMedia")).getItems();
			for(MediaData data: scanned) {
				transactionModel.renewMedia(data.id, 14, core.getLibraryID());
			}
			resetForm();
			return null;
		}
		catch(SQLException ex) {
			Core.debug("One renew attempt failed.");
			return "There was a problem connecting to the database.";
		}
	}

	protected void removeSelected() {
		((JMediaTable)_body.find("tblMedia")).removeSelected();
		JTextField barcode = ((JTextField)_switch.find("txtBarcode"));
		barcode.requestFocus();
	}

	private void makeModels() {
		patronModel = (PatronModel)core.createModel("PatronModel");
		mediaModel = (MediaModel)core.createModel("MediaModel");
		transactionModel = (TransactionModel)core.createModel("TransactionModel");
	}
}
