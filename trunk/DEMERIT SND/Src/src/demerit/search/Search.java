package demerit.search;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

import demerit.Controller;
import demerit.Core;
import demerit.JMediaTable;
import demerit.MediaData;
import demerit.View;
import demerit.models.MediaModel;
import demerit.models.TransactionModel;

/**
 * A controller which is used to search for media in the database.
 */
public class Search extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _header;
	private SwingEngine _side;
	private SwingEngine _mode;
	private SwingEngine _switch;
	MediaModel mediaModel;
	TransactionModel transactionModel;
	
	public Search(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		
		HashMap<String, EventListener> handlers = new HashMap<String, EventListener>();
		handlers.put("txtSearch", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String searchString = ((JTextField)_mode.find("txtSearch")).getText();
				searchRoutine(searchString);
			}
		});
		handlers.put("btnSearch", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String searchString = ((JTextField)_mode.find("txtSearch")).getText();
				searchRoutine(searchString);
			}
		});
		handlers.put("tblMedia", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Core.debug("Received table click");
				MediaData data = (MediaData)ev.getSource();
				ArrayList<Object> par = new ArrayList<Object>();
				par.add(data.barcode);
				core.startController("Management", "MediaView", par, "Media Information");
			}
		});

		_body = view.defineRegion(    View.BODY,    "Modules/Search/Search/body.xml",  handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Search/Search/mode.xml",      handlers);
		
		fillComboBoxes();
		makeModels();
		
		JMediaTable table = (JMediaTable)_body.find("tblMedia");
		ArrayList<Integer> fields = new ArrayList<Integer>();
		fields.add(JMediaTable.FORMAT);
		fields.add(JMediaTable.TITLE);
		fields.add(JMediaTable.AUTHORS);
		fields.add(JMediaTable.CALLNUMBER);
		fields.add(JMediaTable.STATUS);
		table.displayFields(fields);
		return view;
	}
	
	private void makeModels() {
		mediaModel = (MediaModel)core.createModel("MediaModel");
		transactionModel = (TransactionModel)core.createModel("TransactionModel");
	}

	private void fillComboBoxes() {
		JComboBox box = ((JComboBox)_mode.find("cbbSearchIn"));
		box.addItem("All Media");
		box.addItem("Literature");
		box.addItem("Audio");
		box.addItem("Cinema");
		JComboBox by = ((JComboBox)_mode.find("cbbSearchBy"));
		by.addItem("Title/Description");
		by.addItem("Author/Artist/Director");
		by.addItem("Category");
	}

	protected void searchRoutine(String searchString) {
		try {
			Core.debug("Searching the database...");
			JMediaTable table = ((JMediaTable)_body.find("tblMedia"));
			table.removeAllItems();
			JComboBox by = ((JComboBox)_mode.find("cbbSearchBy"));
			ArrayList<Integer> ids = null;
			switch(by.getSelectedIndex()) {
				case 1:
					ids = mediaModel.searchByAuthor(searchString);
					break;
				case 2:
					ids = mediaModel.searchByCategory(searchString);
					break;
				default:
					ids = mediaModel.searchByDescription(searchString);
					break;
			}
			ArrayList<MediaData> media = new ArrayList<MediaData>();
			ArrayList<MediaData> insert = new ArrayList<MediaData>();
			JComboBox box = ((JComboBox)_mode.find("cbbSearchIn"));
			Integer searchType = box.getSelectedIndex();
			for(Integer id: ids) {
				MediaData data = mediaModel.getMedia(id);
				if(transactionModel.isCheckedOut(id))
					data.status = "Checked Out";
				else
					data.status = "Checked In";
				media.add(data);
			}
			if(searchType == 0) {
					insert.addAll(media);
			}
			else {
				for(MediaData data: media) {
					if(data.type == searchType-1) {
						insert.add(data);
					}
				}
			}
			for(MediaData data: insert) {
				table.addItem(data);
			}
			if(insert.size() == 0) {
				JOptionPane.showMessageDialog(null, "No results were found, try a broader search.");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "There was a problem connecting to the database.");
		}
	}
}
