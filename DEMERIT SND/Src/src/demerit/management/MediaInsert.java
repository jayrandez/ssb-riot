package demerit.management;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.*;
import org.swixml.SwingEngine;
import demerit.Controller;
import demerit.Core;
import demerit.MediaData;
import demerit.Validation;
import demerit.View;
import demerit.JAutoCompleteField;
import demerit.JAutoCompleteScroll;
import demerit.models.CategoryModel;
import demerit.models.MediaModel;
import demerit.models.PersonModel;

/**
 * The controller used to insert media into the database.
 */
public class MediaInsert extends Controller {

	private View view;
	private SwingEngine _actions;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _side;
	private SwingEngine _mode;
	HashMap<String, EventListener> handlers;
	MediaModel mediaModel;
	PersonModel personModel;
	CategoryModel categoryModel;
	
	public MediaInsert(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		handlers = new HashMap<String, EventListener>();
		
		handlers.put("btnGenerateBarcode", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((JTextField)_side.find("txtBarcode")).setText(generateBarcode());
			}
		});
		
		handlers.put("btnGenerateCall", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				((JTextField)_side.find("txtCallNumber")).setText(generateCallNumber());
			}
		});
		
		handlers.put("btnReset", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				resetFields();
			}
		});
		
		handlers.put("btnAddMedia", new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				String message = addProcedure();
				if(message == null) {
					resetFields();
					fillAutoCompletes();
					JOptionPane.showMessageDialog(null, "The media was added successfully.");
				}
				else {
					JOptionPane.showMessageDialog(null, message);
				}
			}
		});
		
		handlers.put("cbbType", new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Integer panel = ((JComboBox)_mode.find("cbbType")).getSelectedIndex();
				String[] panels = {
					"Modules/Management/MediaInsert/body_lit.xml",
					"Modules/Management/MediaInsert/body_aud.xml",
					"Modules/Management/MediaInsert/body_cin.xml"
				};
				view.undefineRegion(View.BODY);
				_body = view.defineRegion(View.BODY, panels[panel], handlers);
				fillComboBoxes();
				fillAutoCompletes();
			}
		});
		
		makeModels();

		_actions = view.defineRegion( View.ACTIONS, "Modules/Management/MediaInsert/actions.xml", handlers);
		_body = view.defineRegion(    View.BODY,    "Modules/Management/MediaInsert/body_lit.xml",  handlers);
		_default = view.defineRegion( View.DEFAULT, "Modules/Management/MediaInsert/default.xml", handlers);
		_side = view.defineRegion(    View.SIDE,    "Modules/Management/MediaInsert/side.xml",    handlers);
		_mode = view.defineRegion(    View.MODE,  "Modules/Management/MediaInsert/mode.xml",      handlers);
		
		JComboBox type = ((JComboBox)_mode.find("cbbType"));
		for(int i = 0; i < MediaData.typeValues.length; i++) {
			type.addItem(MediaData.typeValues[i]);
		}
		
		fillAutoCompletes();
		fillComboBoxes();
		
		return view;
	}
	
	private void makeModels() {
		mediaModel = (MediaModel)core.createModel("MediaModel");
		personModel = (PersonModel)core.createModel("PersonModel");
		categoryModel = (CategoryModel)core.createModel("CategoryModel");
	}
	
	private void fillAutoCompletes() {
		try {
			ArrayList<String> categoryList = categoryModel.listCategories();
			ArrayList<String> peopleList = personModel.listPeople();
			
			JAutoCompleteScroll categories = ((JAutoCompleteScroll)_body.find("scrCategories"));
			JAutoCompleteScroll[] peopleBoxes = new JAutoCompleteScroll[3];
			
			peopleBoxes[0] = ((JAutoCompleteScroll)_body.find("scrActors"));
			peopleBoxes[1] = ((JAutoCompleteScroll)_body.find("scrAuthors"));
			peopleBoxes[2] = ((JAutoCompleteScroll)_body.find("scrProducers"));
			
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
		genre.removeAllItems();
		format.removeAllItems();
		for(int j = 0; j < MediaData.genreValues.length; j++) {
			genre.addItem(MediaData.genreValues[j]);
		}
		for(int k = 0; k < MediaData.formatValues.length; k++) {
			format.addItem(MediaData.formatValues[k]);
		}
	}
	
	private void resetFields() {
		((JTextField)_side.find("txtBarcode")).setText("");
		((JTextField)_side.find("txtCallNumber")).setText("");
		((JTextField)_body.find("txtTitle")).setText("");
		((JTextField)_body.find("txtLength")).setText("");
		((JTextField)_body.find("txtCopyright")).setText("");
		((JTextArea)_body.find("tarDescription")).setText("");
		((JAutoCompleteScroll)_body.find("scrAuthors")).clearFields();
		((JAutoCompleteScroll)_body.find("scrCategories")).clearFields();
		
		JTextField edition = ((JTextField)_body.find("txtEdition"));
		if(edition != null) {
			edition.setText("");
		}
		
		JTextField isbn = ((JTextField)_body.find("txtIsbn"));
		if(isbn != null) {
			isbn.setText("");
		}
		
		JAutoCompleteScroll actors = ((JAutoCompleteScroll)_body.find("scrActors"));
		if(actors != null) {
			actors.clearFields();
		}
		
		JAutoCompleteScroll producers = ((JAutoCompleteScroll)_body.find("scrProducers"));
		if(producers != null) {
			producers.clearFields();
		}
	}
	
	private String generateBarcode() {
		String[] array = new String[14];
		int r = 0;
		int sum = 0;
		Random random = new Random();
		String barcode = "";

		//get the entity code
		array[0] = "1";
		
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
	
	private String generateCallNumber() {
		String callnumber = "980.";
		for(int i = 0; i < 4; i++) {
			Integer digit = (int)(Math.random()*9.0);
			callnumber += digit.toString();
		}
		ArrayList<String> authors = ((JAutoCompleteScroll)_body.find("scrAuthors")).getValues();
		if(authors.size() > 0) {
			String author = authors.get(0);
			int substr = 3;
			if(author.length() < 3)
				substr = author.length();
			callnumber += " " + author.substring(0, substr);
		}
		return callnumber;
	}
	
	private String addProcedure() {
		
		ArrayList<String> categories = ((JAutoCompleteScroll)_body.find("scrCategories")).getValues();
		ArrayList<String> authors = ((JAutoCompleteScroll)_body.find("scrAuthors")).getValues();
		ArrayList<String> actors = new ArrayList<String>();
		ArrayList<String> producers = new ArrayList<String>();
		String edition = "";
		String isbn = "";
		
		JTextField editionField = ((JTextField)_body.find("txtEdition"));
		if(editionField != null)
			edition = editionField.getText();
		JTextField isbnField = ((JTextField)_body.find("txtIsbn"));
		if(isbnField != null)
			isbn = isbnField.getText();
		JAutoCompleteScroll actorsField = ((JAutoCompleteScroll)_body.find("scrActors"));
		if(actorsField != null)
			actors = actorsField.getValues();
		JAutoCompleteScroll producersField = ((JAutoCompleteScroll)_body.find("scrProducers"));
		if(producersField != null)
			producers = producersField.getValues();
		
		try {
			MediaData data = new MediaData();
			data.barcode = ((JTextField)_side.find("txtBarcode")).getText();
			data.callNumber = ((JTextField)_side.find("txtCallNumber")).getText();
			data.copyright = ((JTextField)_body.find("txtCopyright")).getText();
			data.description = ((JTextArea)_body.find("tarDescription")).getText();
			data.edition = edition;
			data.format = ((JComboBox)_body.find("cbbFormat")).getSelectedIndex();
			data.genre = ((JComboBox)_body.find("cbbGenre")).getSelectedIndex();
			data.isbn = isbn;
			data.length = ((JTextField)_body.find("txtLength")).getText();
			data.title = ((JTextField)_body.find("txtTitle")).getText();
			data.type = ((JComboBox)_mode.find("cbbType")).getSelectedIndex();
			
			String error = null;
			error = Validation.validateFilled(data.title);
			if(error != null)
				return "Title: " + error;
			error = Validation.validateFilled(data.barcode);
			if(error != null)
				return "Barcode: " + error;
			error = Validation.validateFilled(data.copyright);
			if(error != null)
				return "Copyright: " + error;
			error = Validation.validateFilled(data.callNumber);
			if(error != null)
				return "Call Number: " + error;
			error = Validation.validateFilled(data.description);
			if(error != null)
				return "Description: " + error;
			error = Validation.validateISBN(data.isbn);
			if(error != null)
				return "ISBN: " + error;
			error = Validation.validateFilled(data.length);
			if(error != null)
				return "Length/Tracks/Pages: " + error;
			
			Integer mediaId = mediaModel.insertMedia(data, core.getLibraryID());
			
			for(String category: categories) 
				categoryModel.categoryToMedia(category, mediaId);
			for(String author: authors)
				personModel.authorToMedia(author, mediaId);
			for(String actor: actors)
				personModel.actorToMedia(actor, mediaId);
			for(String producer: producers)
				personModel.producerToMedia(producer, mediaId);
			
			return null;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			Core.debug("Couldn't add media to database.");
			return "There was a problem accessing the database.";
		}
	}
}
