package demerit.systemtools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.swixml.SwingEngine;
import demerit.Controller;
import demerit.Core;
import demerit.SettingsManager;
import demerit.View;
import demerit.models.*;

/**
 * Controller which connects to the database and authenticates the user/pass combo.
 */
public class Login extends Controller {

	private View view;
	private SettingsManager settings;
	private SwingEngine _body;
	private SwingEngine _default;
	private UserModel userModel;
	
	public Login(Core core) {
		super(core);
	}
	
	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		settings = core.requestSettings(this);
		
		HashMap<String, EventListener> events = new HashMap<String, EventListener>();
		
		ActionListener loginAction = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = ((JTextField)_body.find("username")).getText();
				String password = ((JPasswordField)_body.find("password")).getText();
				String code = ((JPasswordField)_body.find("code")).getText();
				authenticateUser(username, password, code);
			}
		};
		
		events.put("username", loginAction);
		events.put("password", loginAction);
		events.put("code", loginAction);
		events.put("login", loginAction);
		
		_body = view.defineRegion(View.BODY, "Modules/SystemTools/Login/body.xml", events);
		_default = view.defineRegion(View.DEFAULT, "Modules/SystemTools/Login/default.xml", events);
		
		if(settings.needsExternalKey()) {
			_body.find("codeholder").setVisible(true);
		}
		
		view.addComponentListener(new java.awt.event.ComponentAdapter() {   
			public void componentShown(java.awt.event.ComponentEvent e) {
				_body.find("username").requestFocusInWindow();
			}
		});

		return view;
	}
	
	private void authenticateUser(String username, String password, String code) {
		try {
			Integer library = Integer.parseInt(settings.getLibraryID());
			if(library != null) {
				settings.useExternalKey(code);
				if(core.loginUser(username, password, library)) {
					core.stopController(this);
					core.loadProcedure();
					return;
				}
			}
		}
		catch(Exception ex) {}
		((JLabel)_body.find("error")).setText("Could not log in with this information.");
	}
}
