package demerit;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import demerit.models.UserModel;

/**
 * The starting class of the "Demerit" library system.
 * Creates the AppFrame window and manages the SettingsManager
 * Creates the Class Loader to allow dynamic loading of modules.
 * Manages a bit of the login procedure.
 */
public class Core {
	
	public static Boolean developmental;
	public static final String applicationName = "Demerit";
	public static final String rootPackage = "demerit";
	public static Double dailyCharge = .05;
	
	private ExtendedClassLoader cld;
	private AppFrame window;
	private SettingsManager settings;
	private HashMap<Controller, View> controllers;
	private HashMap<String, String> helpFiles;
	
	private Boolean loggedIn;
	private Boolean administrator;
	private Integer userID;
	private Integer library;

	public Core() {
		
		cld = new ExtendedClassLoader();
		controllers = new HashMap<Controller, View>();
		helpFiles = new HashMap<String, String>();
		
		File settingsFile = new File("Data/settings.xml");
		settings = new SettingsManager(settingsFile);
		
		window = new AppFrame(this);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		HashMap<String, ArrayList<MenuEntry>> menuBar = new HashMap<String, ArrayList<MenuEntry>>();
		ArrayList<MenuEntry> fileMenu = new ArrayList<MenuEntry>();
		fileMenu.add(new MenuEntry(applicationName, "Close", "EXIT", false, false));
		menuBar.put(applicationName, fileMenu);
		window.createMenuBar(menuBar);
		
		if(!settings.isSane()) {
			Core.debug("Settings not sane. Running Setup.");
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(settings);
			startController("SystemTools", "Installer", parameters, "Installer");
		}
		else {
			Core.debug("Settings sane. Running Login.");
			Core.debug("Login will require external key: " + settings.needsExternalKey());
			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(settings);
			startController("SystemTools", "Login", parameters, "Log In");
		}
	}
	
	public Boolean isLoggedIn() {
		return loggedIn;
	}
	
	public Boolean isAdministrator() {
		return administrator;
	}
	
	public Integer getLibraryID() {
		return library;
	}
	
	public Integer getUserID() {
		return userID;
	}
	
	public SettingsManager requestSettings(Controller controller) {
		if(controller instanceof demerit.systemtools.Installer ||
				controller instanceof demerit.systemtools.Login ||
					controller instanceof demerit.systemtools.Settings) {
			return settings;
		}
		return null;
	}
	
	public HashMap<String, String> requestHelpFiles(Controller controller) {
		if(controller instanceof demerit.systemtools.HelpViewer){
			return helpFiles;
		}
		return null;
	}
	
	public Controller startController(String moduleName, String controllerName, ArrayList<Object> parameters) {
		return startController(moduleName, controllerName, parameters, "New Controller");
	}
	
	public Controller startController(String moduleName, String controllerName, ArrayList<Object> parameters, String label) {
		ArrayList<Object> constructor = new ArrayList<Object>();
		constructor.add(this);
		String objectName = rootPackage + "." + moduleName.toLowerCase() + "." + controllerName;
		try {
			Core.debug("Instantiating controller: " + objectName);
			Controller controller = (Controller)dynamicNew(objectName, constructor);
			View view = controller.init(parameters);
			if(view != null) {
				controllers.put(controller, view);
				window.addView(view, label);
			}
			return controller;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Core.message("It appears part of the module \"" + moduleName + "\" isn't functioning.\nYou may need to reinstall the module.");
			Core.debug("Couldn't instantiate controller.");
			return null;
		}
	}

	public void stopController(View view) {
		Controller controller = null;
		for(Controller c: controllers.keySet()) {
			if(controllers.get(c) == view) {
				controller = c;
				break;
			}
		}
		stopController(controller);
	}
	
	public void stopController(Controller controller) {
		if(controller != null) {
			View view = controllers.get(controller);
			controllers.remove(controller);
			window.removeView(view);
		}
	}
	
	public Boolean loginUser(String username, String password, Integer library) {
		Integer result = -1;
		UserModel userModel = (UserModel)createModel("UserModel");
		if(userModel != null) {
			try {
				result = userModel.isValid(username, password, library);
				if(result >= 0) {
					loggedIn = true;
					administrator = userModel.isAdministrator(username, library);
					this.library = library;
					this.userID = result;
					return true;
				}
			}
			catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}
	
	private void logoutUser() {
		loggedIn = false;
		unloadProcedure();
	}
	
	public void loadProcedure() {
		if(!developmental) {
			cld = new DistributionClassLoader();
		}
		else {
			cld = new DevelopmentClassLoader();
		}
		
		ArrayList<String> newModuleNames = cld.findAvailableModules(settings.flushKnownModules());
		for(String moduleName: newModuleNames) {
			Integer result = JOptionPane.showConfirmDialog(window, "Do you want to use the module: " + moduleName + "?");
			if(result == JOptionPane.YES_OPTION) {
				settings.enableModule(moduleName);
			}
			else {
				settings.disableModule(moduleName);
			}
		}
		
		ArrayList<String> lostModuleNames = cld.enableModules(settings.flushAllowedModules());
		for(String moduleName: lostModuleNames) {
			settings.forgetModule(moduleName);
		}
		
		settings.saveDocument();
		
		HashMap<String, ArrayList<MenuEntry>> menuEntries = new HashMap<String, ArrayList<MenuEntry>>();
		ArrayList<MenuEntry> autostartControllers = new ArrayList<MenuEntry>();
		
		ArrayList<String> loadedModules = cld.getEnabledModules();
		for(String moduleName: loadedModules) {
			String moduleClass = rootPackage + ".headers." + moduleName;
			try {
				Header moduleHeader = (Header)dynamicNew(moduleClass, new ArrayList<Object>());
				HashMap<String, String> helpItems = moduleHeader.getHelpEntries();
				if(helpItems != null) {
					for(String name: helpItems.keySet()) {
						helpFiles.put(name, helpItems.get(name));
					}
				}
				ArrayList<MenuEntry> menuItems = moduleHeader.getMenuEntries();
				if(menuItems != null) {
					for(MenuEntry item: menuItems) {
						ArrayList<MenuEntry> finalEntries;
						if(menuEntries.containsKey(item.menu))
							finalEntries = menuEntries.get(item.menu);
						else
							finalEntries = new ArrayList<MenuEntry>();
						item.source = moduleName;
						if(!(item.administrative && !administrator)) {
							finalEntries.add(item);
							menuEntries.put(item.menu, finalEntries);
							if(item.autostart == true) {
								autostartControllers.add(item);
							}
						}
					}
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		ArrayList<MenuEntry> fileMenu;
		if(menuEntries.containsKey(applicationName)) {
			fileMenu = menuEntries.get(applicationName);
		}
		else {
			fileMenu = new ArrayList<MenuEntry>();
		}
		fileMenu.add(new MenuEntry(applicationName, "Log Out", "LOGOUT", false, false));
		fileMenu.add(new MenuEntry(applicationName, "Exit", "EXIT", false, false));
		menuEntries.put(applicationName, fileMenu);
		window.createMenuBar(menuEntries);
		
		for(MenuEntry entry: autostartControllers) {
			startController(entry.source, entry.controller, entry.parameters, entry.label);
		}
	}
	
	private void unloadProcedure() {
		for(View view: controllers.values()) {
			window.removeView(view);
		}
		
		Core.debug("Login will require external key: " + settings.needsExternalKey());
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(settings);
		startController("SystemTools", "Login", parameters, "Log In");
		
		HashMap<String, ArrayList<MenuEntry>> menuBar = new HashMap<String, ArrayList<MenuEntry>>();
		ArrayList<MenuEntry> fileMenu = new ArrayList<MenuEntry>();
		fileMenu.add(new MenuEntry(applicationName, "Close", "EXIT", false, false));
		menuBar.put(applicationName, fileMenu);
		window.createMenuBar(menuBar);
	}
	
	public void menuItemClicked(AppFrame securityCheck, MenuEntry entry) {
		if(securityCheck == window) {
			if(entry.controller.equals("LOGOUT")) {
				logoutUser();
			}
			else if(entry.controller.equals("EXIT")) {
				window.dispose();
			}
			else {
				startController(entry.source, entry.controller, entry.parameters, entry.label);
			}
		}
	}
	
	public Model createModel(String modelName) {
		ArrayList<Object> constructor = new ArrayList<Object>();
		constructor.add(settings.getConnectionData());
		String objectName = rootPackage + ".models." + modelName;
		try {
			Model model = (Model)dynamicNew(objectName, constructor);
			if(!model.isSane()) {
				return null;
			}
			return model;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Core.debug("Couldn't instantiate model.");
			return null;
		}
	}
	
	private Object dynamicNew(String className, ArrayList<Object> parameters) throws Exception {
		Class<?> objectClass = cld.loadClass(className);
		if(objectClass == null)
			throw new ClassNotFoundException();
		
		Class<?>[] paramTypes = new Class<?>[parameters.size()];
		Object[] paramObjects = new Object[parameters.size()];
		
		for(int i = 0; i < parameters.size(); i++) {
			paramTypes[i] = parameters.get(i).getClass();
			paramObjects[i] = parameters.get(i);
		}
		
		Constructor<?> objectConstructor = objectClass.getConstructor(paramTypes);
		Object object = (Object)objectConstructor.newInstance(paramObjects);

		return object;
	}
	
	public static void main(String[] args) {
		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		
		Integer lookAndFeel = 0;
		if(argList.contains("-platform"))
			lookAndFeel = 1;
		else if(argList.contains("-motif"))
			lookAndFeel = 2;
		else if(argList.contains("-gtk"))
			lookAndFeel = 3;
		
		if(argList.contains("-developmental")) {
			developmental = true;
		}
		else {
			developmental = false;
		}
		
		setLookAndFeel(lookAndFeel);
		Core core = new Core();
	}
	
	public static void setLookAndFeel(Integer lookAndFeel) {
		switch(lookAndFeel) {
			case 1:
				try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch(Exception e) {}
				break;
			case 2:
				try{UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");} catch(Exception e) {}
				break;
		}
	}
	
	public static void debug(String message) {
		if(developmental) {
			System.out.println(message);
		}
	}
	
	public static void fatal(String source) {
		JOptionPane.showMessageDialog(null, "There was a fatal error in the component \"" + source + "\". The program must exit.");
		System.exit(0);
	}
	
	public static void message(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
}
