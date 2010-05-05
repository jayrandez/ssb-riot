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
import demerit.models.MediaModel;
import demerit.models.PatronModel;
import demerit.models.TransactionModel;

/**
 * A controller which makes it easy for librarians to check in large quantities
 * of books (on a return rack) at a time.
 */
public class CheckIn extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _header;
	private SwingEngine _mode;
	private SwingEngine _switch;
	HashMap<String, EventListener> handlers;
	MediaModel mediaModel;
	TransactionModel transactionModel;
	
	public CheckIn(Core core) {
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
		
		
		handlers.put("btnCheckIn", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = checkinProceure();
				if(message != null) {
					JOptionPane.showMessageDialog(null, message);
				}
				else {
					resetForm();
				}
			}
		});

		handlers.put("txtBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMedia();
			}
		});

		_actions = view.defineRegion( View.ACTIONS, "Modules/Transaction/CheckIn/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Transaction/CheckIn/body.xml",  handlers);
		_default = view.defineRegion( View.DEFAULT, "Modules/Transaction/CheckIn/default.xml", handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Transaction/CheckIn/mode.xml",      handlers);
		
		view.addComponentListener(new java.awt.event.ComponentAdapter() {   
			public void componentShown(java.awt.event.ComponentEvent e) {
				resetForm();
			}
		});
		
		JMediaTable table = (JMediaTable)_body.find("tblMedia");
		ArrayList<Integer> fields = new ArrayList<Integer>();
		fields.add(JMediaTable.TYPE);
		fields.add(JMediaTable.FORMAT);
		fields.add(JMediaTable.TITLE);
		fields.add(JMediaTable.AUTHORS);
		fields.add(JMediaTable.CALLNUMBER);
		table.displayFields(fields);
		
		makeModels();
		
		return view;
	}
	
	protected void selectMedia() {
		try {
			JTextField barcode = ((JTextField)_mode.find("txtBarcode"));
			MediaData data = mediaModel.getMedia(barcode.getText());
			data.selected = true;
			JMediaTable table = (JMediaTable)_body.find("tblMedia");
			table.addItem(data);
			barcode.setText("");
			barcode.requestFocus();
		}
		catch (SQLException e) {
			Core.debug("Add media to table failed.");
			JOptionPane.showMessageDialog(null, "Couldn't find media in the database.");
		}
	}

	protected String checkinProceure() {
		try {
			ArrayList<MediaData> scanned = ((JMediaTable)_body.find("tblMedia")).getItems();
			for(MediaData data: scanned) {
				transactionModel.checkInMedia(data.id, core.getLibraryID());
			}
			resetForm();
			return null;
		}
		catch(SQLException ex) {
			Core.debug("One checkin attempt failed.");
			return "There was a problem connecting to the database.";
		}
	}

	protected void resetForm() {
		JTextField barcode = ((JTextField)_mode.find("txtBarcode"));
		JMediaTable table = ((JMediaTable)_body.find("tblMedia"));
		table.removeAllItems();
		barcode.requestFocus();
	}
	
	protected void removeSelected() {
		((JMediaTable)_body.find("tblMedia")).removeSelected();
		JTextField barcode = ((JTextField)_mode.find("txtBarcode"));
		barcode.requestFocus();
	}

	private void makeModels() {
		mediaModel = (MediaModel)core.createModel("MediaModel");
		transactionModel = (TransactionModel)core.createModel("TransactionModel");
	}
}
