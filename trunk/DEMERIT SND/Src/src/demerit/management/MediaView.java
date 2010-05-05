package demerit.management;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.swixml.SwingEngine;
import org.swixml.XVBox;

import demerit.Controller;
import demerit.Core;
import demerit.JAutoCompleteScroll;
import demerit.JLinkLabel;
import demerit.MediaData;
import demerit.Validation;
import demerit.View;
import demerit.models.CategoryModel;
import demerit.models.MediaModel;
import demerit.models.PersonModel;
import demerit.models.TransactionModel;

/**
 * The user used to view information for specific media.
 */
public class MediaView extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _mode;
	MediaModel mediaModel;
	CategoryModel categoryModel;
	PersonModel personModel;
	TransactionModel transactionModel;
	HashMap<String, EventListener> handlers;
	
	MediaData current;
	
	public MediaView(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		
		handlers = new HashMap<String, EventListener>();
		
		handlers.put("txtBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String barcode = ((JTextField)_mode.find("txtBarcode")).getText();
				if(!selectMedia(barcode)) {
					JOptionPane.showMessageDialog(null, "Couldn't find the media in the database.");
				}
			}
		});
		handlers.put("btnCheckIn", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkInMedia();
			}
		});
		handlers.put("btnUpdate", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = updateMedia();
				if(message != null) {
					JOptionPane.showMessageDialog(null, message);
				}
			}
		});
		handlers.put("btnRemove", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeMedia();
			}
		});

		_actions = view.defineRegion( View.ACTIONS, "Modules/Management/MediaView/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/MediaView/body.xml",  handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Management/MediaView/mode.xml",      handlers);
		
		clearForm();
		makeModels();
		
		if(parameters.size() > 0) {
			String barcode = (String) parameters.get(0);
			selectMedia(barcode);
		}
		
		if(!core.isAdministrator()) {
			JButton update = (JButton)_actions.find("btnUpdate");
			JButton remove = (JButton)_actions.find("btnRemove");
			update.setVisible(false);
			remove.setVisible(false);
		}
		
		return view;
	}
	
	private void prepareFields() {
		for(int j = 0; j < 2; j++) {
			XVBox parent = (XVBox)((JPanel)_body.find("vbxMediaView")).getComponent(j);
			for(int i = 1-j; i < parent.getComponentCount(); i++) {
				JPanel row = (JPanel)parent.getComponent(i);
				JLabel label = (JLabel)row.getComponent(1);
				JComponent editor = (JComponent)row.getComponent(2);
				JLinkLabel link = (JLinkLabel)row.getComponent(3);
				link.addActionListener(new FieldLinkListener(editor, label, link));
				if(!core.isAdministrator()) {
					link.setVisible(false);
				}
			}
		}
	}
	
	private void fillAutoCompletes() {
		try {
			ArrayList<String> categoryList = categoryModel.listCategories();
			ArrayList<String> peopleList = personModel.listPeople();
			
			JAutoCompleteScroll categories = ((JAutoCompleteScroll)_body.find("autCategories"));
			JAutoCompleteScroll[] peopleBoxes = new JAutoCompleteScroll[3];
			
			peopleBoxes[0] = ((JAutoCompleteScroll)_body.find("autActors"));
			peopleBoxes[1] = ((JAutoCompleteScroll)_body.find("autAuthors"));
			peopleBoxes[2] = ((JAutoCompleteScroll)_body.find("autProducers"));
			
			if(peopleList != null) {
				for(int i = 0; i < 3; i++) {
					if(peopleBoxes[i] != null) {
						peopleBoxes[i].setPossibilities(peopleList);
					}
				}
			}
			if(categoryList != null) {
				categories.setPossibilities(categoryList);
			}
		}
		catch (SQLException e) {
			Core.debug("Couldn't fill people/category autocompletes.");
		}
	}
	
	private void fillComboBoxes() {
		JComboBox genre = ((JComboBox)_body.find("cbbGenre"));
		JComboBox format = ((JComboBox)_body.find("cbbFormat"));
		JComboBox type = ((JComboBox)_body.find("cbbType"));
		for(int i = 0; i < MediaData.typeValues.length; i++) {
			type.addItem(MediaData.typeValues[i]);
		}
		for(int j = 0; j < MediaData.genreValues.length; j++) {
			genre.addItem(MediaData.genreValues[j]);
		}
		for(int k = 0; k < MediaData.formatValues.length; k++) {
			format.addItem(MediaData.formatValues[k]);
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

	protected void removeMedia() {
		try {
			mediaModel.deleteMedia(current.id);
			clearForm();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "There was a problem connecting to the database.");
		}
	}
	
	protected void checkInMedia() {
		try {
			transactionModel.checkInMedia(current.id, core.getLibraryID());
			clearForm();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "There was a problem connecting to the database.");
		}
	}

	private void clearForm() {
		view.undefineRegion(View.BODY);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/MediaView/body.xml",  handlers);
		prepareFields();
		fillComboBoxes();
		((JPanel)_body.find("vbxMediaView")).setVisible(false);
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

	protected String updateMedia() {
		try {
			MediaData updated = new MediaData();
			updated.title = current.title;
			updated.id = current.id;
			
			JPanel a;
			a = (JPanel)_body.find("pnlType");
			String value = (String)getRowValue(a);
			for(int i = 0; i < MediaData.typeValues.length; i++) {
				if(MediaData.typeValues[i].equals(value)) {
					updated.type = i;
					break;
				}
			}
			a = (JPanel)_body.find("pnlFormat");
			value = (String)getRowValue(a);
			for(int i = 0; i < MediaData.formatValues.length; i++) {
				if(MediaData.formatValues[i].equals(value)) {
					updated.format = i;
					break;
				}
			}
			a = (JPanel)_body.find("pnlGenre");
			value = (String)getRowValue(a);
			for(int i = 0; i < MediaData.genreValues.length; i++) {
				if(MediaData.genreValues[i].equals(value)) {
					updated.genre = i;
					break;
				}
			}
			a = (JPanel)_body.find("pnlLength");
			updated.length = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlCallNumber");
			updated.callNumber = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlBarcode");
			updated.barcode = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlCopyright");
			updated.copyright = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlDescription");
			updated.description = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlEdition");
			updated.edition = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlISBN");
			updated.isbn = (String)getRowValue(a);
			a = (JPanel)_body.find("pnlProducers");
			updated.producers = (ArrayList<String>)getRowValue(a);
			a = (JPanel)_body.find("pnlActors");
			updated.actors = (ArrayList<String>)getRowValue(a);
			a = (JPanel)_body.find("pnlAuthors");
			updated.authors = (ArrayList<String>)getRowValue(a);
			a = (JPanel)_body.find("pnlCategories");
			updated.categories = (ArrayList<String>)getRowValue(a);
			
			String error = null;
			error = Validation.validateFilled(updated.title);
			if(error != null)
				return "Title: " + error;
			error = Validation.validateFilled(updated.barcode);
			if(error != null)
				return "Barcode: " + error;
			error = Validation.validateFilled(updated.copyright);
			if(error != null)
				return "Copyright: " + error;
			error = Validation.validateFilled(updated.callNumber);
			if(error != null)
				return "Call Number: " + error;
			error = Validation.validateFilled(updated.description);
			if(error != null)
				return "Description: " + error;
			error = Validation.validateISBN(updated.isbn);
			if(error != null)
				return "ISBN: " + error;
			error = Validation.validateFilled(updated.length);
			if(error != null)
				return "Length: " + error;
			
			categoryModel.removeMediaCategories(updated.id);
			personModel.removeMediaPeople(updated.id);
			mediaModel.updateMedia(updated.id, updated, core.getLibraryID());
			for(String category: updated.categories)
				categoryModel.categoryToMedia(category, updated.id);
			for(String author: updated.authors)
				personModel.authorToMedia(author, updated.id);
			for(String producer: updated.producers)
				personModel.producerToMedia(producer, updated.id);
			for(String actor: updated.actors)
				personModel.actorToMedia(actor, updated.id);
			
			clearForm();
			selectMedia(updated.barcode);
			return null;
		}
		catch(SQLException ex) {
			return "There was a problem connecting to the database.";
		}
	}

	protected boolean selectMedia(String barcode) {
		try {
			clearForm();
			fillAutoCompletes();
			current = mediaModel.getMedia(barcode);
			
			((JLabel)_body.find("lblTitle")).setText(current.title);
			
			JPanel a;
			a = (JPanel)_body.find("pnlType");
			setRowValue(a, MediaData.typeValues[current.type]);
			a = (JPanel)_body.find("pnlFormat");
			setRowValue(a, MediaData.formatValues[current.format]);
			a = (JPanel)_body.find("pnlGenre");
			setRowValue(a, MediaData.genreValues[current.genre]);
			a = (JPanel)_body.find("pnlLength");
			setRowValue(a, current.length);
			a = (JPanel)_body.find("pnlCallNumber");
			setRowValue(a, current.callNumber);
			a = (JPanel)_body.find("pnlBarcode");
			setRowValue(a, current.barcode);
			a = (JPanel)_body.find("pnlCopyright");
			setRowValue(a, current.copyright);
			a = (JPanel)_body.find("pnlDescription");
			setRowValue(a, current.description);
			a = (JPanel)_body.find("pnlEdition");
			setRowValue(a, current.edition);
			a = (JPanel)_body.find("pnlISBN");
			setRowValue(a, current.isbn);
			a = (JPanel)_body.find("pnlProducers");
			setRowValue(a, current.producers);
			a = (JPanel)_body.find("pnlActors");
			setRowValue(a, current.actors);
			a = (JPanel)_body.find("pnlAuthors");
			setRowValue(a, current.authors);
			a = (JPanel)_body.find("pnlCategories");
			setRowValue(a, current.categories);
			
			((JPanel)_body.find("vbxMediaView")).setVisible(true);
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
		mediaModel = (MediaModel)core.createModel("MediaModel");
		categoryModel = (CategoryModel)core.createModel("CategoryModel");
		personModel = (PersonModel)core.createModel("PersonModel");
		transactionModel = (TransactionModel)core.createModel("TransactionModel");
	}
	
	
}
