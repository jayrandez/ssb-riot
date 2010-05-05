package demerit.systemtools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.swixml.SwingEngine;

import demerit.Controller;
import demerit.Core;
import demerit.JMediaTable;
import demerit.MediaData;
import demerit.SettingsManager;
import demerit.View;
import demerit.models.LibraryModel;
import demerit.models.UserModel;

/**
 * Controller which allows an administrator to add users and manage moduels.
 */
public class Settings extends Controller {
	
	private View view;
	private SettingsManager settings;
	private SwingEngine _body;
	private SwingEngine _actions;
	private SwingEngine _default;
	private HashMap<String, EventListener> handlers;
	UserModel userModel;
	LibraryModel libraryModel;
	
	JMediaTable users;
	JMediaTable modules;

	public Settings(Core core) {
		super(core);
	}

	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		settings = core.requestSettings(this);
		handlers = new HashMap<String, EventListener>();
		
		handlers.put("btnSave", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
				resetForm();
			}
		});
		
		handlers.put("btnAdd", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addUser();
				resetForm();
			}
		});

		handlers.put("btnRemove", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelected();
				resetForm();
			}
		});
		
		_body = view.defineRegion(View.BODY, "Modules/SystemTools/Settings/body.xml", handlers);
		_actions = view.defineRegion(View.ACTIONS, "Modules/SystemTools/Settings/actions.xml", handlers);
		_default = view.defineRegion(View.DEFAULT, "Modules/SystemTools/Settings/default.xml", handlers);
		
		users = (JMediaTable)_body.find("tblUsers");
		modules = (JMediaTable)_body.find("tblModules");
		ArrayList<Integer> userFields = new ArrayList<Integer>();
		userFields.add(JMediaTable.SELECTED);
		userFields.add(JMediaTable.TITLE);
		userFields.add(JMediaTable.AUTHORS);
		ArrayList<Integer> moduleFields = new ArrayList<Integer>();
		moduleFields.add(JMediaTable.SELECTED);
		moduleFields.add(JMediaTable.TITLE);
		users.displayFields(userFields);
		modules.displayFields(moduleFields);
		users.hideHeader();
		modules.hideHeader();
		
		makeModels();
		resetForm();
		
		return view;
	}

	private void makeModels() {
		userModel = (UserModel)core.createModel("UserModel");
		libraryModel = (LibraryModel)core.createModel("LibraryModel");
	}

	protected void removeSelected() {
		try {
			ArrayList<MediaData> entries = users.getItems();
			for(MediaData entry: entries) {
				if(entry.selected == true) {
					userModel.deleteUser(entry.title, core.getLibraryID());
				}
			}
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "The users could not be updated.");
		}
	}

	protected void addUser() {
		JTextField username = (JTextField)_body.find("txtUser");
		JPasswordField password = (JPasswordField)_body.find("txtPass");
		JCheckBox administrator = (JCheckBox)_body.find("chkAdmin");
		try {
			userModel.addUser(username.getText(), password.getText(), generateSalt(), core.getLibraryID(), administrator.isSelected());
		}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, "The user couldn't be added.");
		}
		username.setText("");
		password.setText("");
		administrator.setSelected(false);
	}
	
	protected String generateSalt() {
		String salt = "";
		String[] possibilities = new String[] {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
		};
		for(int i = 0; i < 10; i++) {
			salt += possibilities[(int)(Math.random()*possibilities.length)];
		}
		return salt;
	}

	protected void saveSettings() {
		ArrayList<MediaData> entries = modules.getItems();
		for(MediaData entry: entries) {
			if(entry.selected){
				settings.enableModule(entry.title);
			}
			else {
				settings.disableModule(entry.title);
			}
		}
		settings.saveDocument();
		JOptionPane.showMessageDialog(null, "You will have to restart the program for changes to take affect.");
	}

	private void resetForm() {
		users.removeAllItems();
		modules.removeAllItems();
		
		ArrayList<String> allModules = settings.flushKnownModules();
		ArrayList<String> allowedModules = settings.flushAllowedModules();
		
		allModules.remove("SystemTools");
		allowedModules.remove("SystemTools");
		
		for(String name: allowedModules) {
			MediaData data = new MediaData();
			data.title = name;
			data.selected = true;
			modules.addItem(data);
			allModules.remove(name);
		}
		
		for(String name: allModules) {
			MediaData data = new MediaData();
			data.title = name;
			data.selected = false;
			modules.addItem(data);
		}
		
		try {
			ArrayList<String> usernames = userModel.listUsers(core.getLibraryID());
			for(String username: usernames) {
				MediaData data = new MediaData();
				data.title = username;
				if(userModel.isAdministrator(username, core.getLibraryID())) {
					ArrayList<String> entry = new ArrayList<String>();
					entry.add("Administrator");
					data.authors = entry;
				}
				else {
					ArrayList<String> entry = new ArrayList<String>();
					entry.add("");
					data.authors = entry;
				}
				data.selected = false;
				users.addItem(data);
			}
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Couldn't retrieve a list of users.");
		}
	}
}
