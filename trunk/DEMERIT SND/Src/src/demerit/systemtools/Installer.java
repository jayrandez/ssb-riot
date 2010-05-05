package demerit.systemtools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import demerit.ConnectionData;
import demerit.Controller;
import demerit.Core;
import demerit.SettingsManager;
import demerit.View;
import demerit.models.LibraryModel;
import demerit.models.UserModel;

import org.swixml.SwingEngine;

/**
 * Controller used to set up the settings.xml file (in effect installing the program)
 * Manages the file using a SettingsManager obtained from core.
 */
public class Installer extends Controller {
	private View view;
	private SettingsManager settings;
	private SwingEngine _body;
	private SwingEngine _default;
	private SwingEngine _actions;
	private Integer currentPage;
	private HashMap<String, EventListener> handlers;
	private ArrayList<Integer> libraryIds;
	private Boolean hasAdministrator;
	private Integer selectedLibrary;
	
	LibraryModel libraryModel;
	UserModel userModel;
	
	public Installer(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		settings = core.requestSettings(this);
		handlers = new HashMap<String, EventListener>();
		libraryIds = new ArrayList<Integer>();
		currentPage = 1;
		
		handlers.put("btnPrevious", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				savePageData();
				currentPage--;
				definePageBody();
				preparePageData();
			}
		});
		
		handlers.put("btnNext", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(savePageData()) {
					currentPage++;
					definePageBody();
					preparePageData();
				}
			}
		});
		
		handlers.put("btnFinish", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(savePageData()) {
					currentPage++;
					definePageBody();
					preparePageData();
				}
			}
		});
		
		handlers.put("btnCancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		handlers.put("btnHelp", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		handlers.put("cbbLibrary", new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				try {
					JComboBox libraryBox = (JComboBox)_body.find("cbbLibrary");
					Integer selected = libraryBox.getSelectedIndex()-1;
					Integer libraryId = libraryIds.get(selected);
					selectedLibrary = libraryId;
					Core.debug(libraryId.toString());
					if(libraryModel.hasAdministrator(libraryId)) {
						hasAdministrator = true;
						Core.debug("Administrator exists");
						_body.find("txtExistant").setVisible(true);
						_body.find("txtNonexistant").setVisible(false);
					}
					else {
						hasAdministrator = false;
						Core.debug("Administrator does not exist");
						_body.find("txtNonexistant").setVisible(true);
						_body.find("txtExistant").setVisible(false);
					}
					_body.find("pnlUserouter").setVisible(true);
					_body.find("pnlPassouter").setVisible(true);
				}
				catch (Exception e)
				{
				}
			}
		});
		
		_actions = view.defineRegion(View.ACTIONS, "Modules/SystemTools/Installer/actions.xml", handlers);
		_default = view.defineRegion(View.DEFAULT, "Modules/SystemTools/Installer/default.xml", handlers);
		
		definePageBody();
		preparePageData();
		
		return view;
	}
	
	private void makeModels() {
		libraryModel = (LibraryModel)core.createModel("LibraryModel");
		userModel = (UserModel)core.createModel("UserModel");
	}
	
	private void fillComboBoxes() {
		try {
			JComboBox libraryBox = (JComboBox)_body.find("cbbLibrary");
			if(libraryBox != null) {
				libraryBox.addItem("");
				HashMap<Integer, String> libraries = libraryModel.listLibraries();
				for(Integer id: libraries.keySet()) {
					libraryIds.add(id);
					libraryBox.addItem(libraries.get(id));
				}
			}
		}
		catch (SQLException e) {
			Core.debug("Couldn't find libraries.");
		}
	}

	private void definePageBody() {
		view.undefineRegion(View.BODY);
		_body = view.defineRegion(View.BODY, "Modules/SystemTools/Installer/body_" + currentPage + ".xml", handlers);
		fillComboBoxes();
	}
	
	private void preparePageData() {
		switch(currentPage) {
			case 1:
				_actions.find("btnPrevious").setEnabled(false);
				_default.find("btnNext").setEnabled(true);
				_default.find("btnFinish").setEnabled(false);
				break;
			case 2:
				_actions.find("btnPrevious").setEnabled(true);
				_default.find("btnNext").setEnabled(true);
				_default.find("btnFinish").setEnabled(false);
				ConnectionData original = settings.getConnectionData();
				((JTextField)_body.find("txtHost")).setText(original.host);
				((JTextField)_body.find("txtPort")).setText(original.port);
				((JTextField)_body.find("txtDBName")).setText(original.dbname);
				((JTextField)_body.find("txtUser")).setText(original.user);
				((JPasswordField)_body.find("pssPass")).setText(original.pass);
				break;
			case 3:
				_actions.find("btnPrevious").setEnabled(true);
				_default.find("btnNext").setEnabled(false);
				_default.find("btnFinish").setEnabled(true);
				JComboBox libraryBox = (JComboBox)_body.find("cbbLibrary");
				Integer originalLibrary = -1;
				try {
					Integer.parseInt(settings.getLibraryID());
				}
				catch(Exception ex) {}
				Integer i = 0;
				for(Integer id: libraryIds) {
					if(id.equals(originalLibrary)) {
						libraryBox.setSelectedIndex(i);
						break;
					}
					i++;
				}
				break;
			default:
				view.undefineRegion(View.ACTIONS);
				view.undefineRegion(View.DEFAULT);
				break;
		}
	}
	
	private String generateSalt() {
		String salt = "";
		for(int i = 0; i < 10; i++) {
			Integer digit = (int)(Math.random()*9.0);
			salt += digit.toString();
		}
		return salt;
	}
	
	private Boolean savePageData() {
		switch(currentPage) {
		case 1:
			return true;
		case 2:
			String host = ((JTextField)_body.find("txtHost")).getText();
			String port = ((JTextField)_body.find("txtPort")).getText();
			String dbName = ((JTextField)_body.find("txtDBName")).getText();
			String user = ((JTextField)_body.find("txtUser")).getText();
			String pass = ((JPasswordField)_body.find("pssPass")).getText();
			settings.setDatabaseInfo(host, port, dbName);
			settings.setUserCredentials(user, pass);
			makeModels();
			if(userModel == null || libraryModel == null) {
				((JLabel)_body.find("lblError")).setText("This information couldn't be used to connect to the database.");
				return false;
			}
			return true;
		case 3:
			Boolean okay = true;
			try {
				String username = ((JTextField)_body.find("txtUser")).getText();
				String password = ((JPasswordField)_body.find("pssPass")).getText();
				if(hasAdministrator) {
					if(userModel.isValid(username, password, selectedLibrary) >= 0 || !userModel.isAdministrator(username, selectedLibrary)) {
						okay = false;
					}
				}
				else {
					userModel.addUser(username, password, generateSalt(), selectedLibrary, true);
				}
				settings.setLibraryID(selectedLibrary.toString());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				okay = false;
			}
			if(okay) {
				settings.enableModule("SystemTools");
				settings.saveDocument();
				return true;
			}
			else {
				((JLabel)_body.find("lblError")).setText("The system can not be set up with this username/password.");
				return false;
			}
		default:
			return true;
		}
	}
}