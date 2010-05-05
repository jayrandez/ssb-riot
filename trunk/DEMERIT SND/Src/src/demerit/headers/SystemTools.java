package demerit.headers;

import java.util.ArrayList;
import java.util.HashMap;

import demerit.Core;
import demerit.Header;
import demerit.MenuEntry;

/**
 * The system tools module contains all essential controllers for managing the program
 * (setup, installer, login)
 */
public class SystemTools extends Header {

	public ArrayList<MenuEntry> getMenuEntries() {
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		entries.add(new MenuEntry("Help", "Help Topics", "HelpViewer", false, false));
		entries.add(new MenuEntry(Core.applicationName, "Settings", "Settings", false, true));
		entries.add(new MenuEntry(Core.applicationName, "Installer", "Installer", false, true));
		return entries;
	}

	public HashMap<String, String> getHelpEntries() {
		HashMap<String, String> entries = new HashMap<String, String>();
		entries.put("About Demerit", "Modules/SystemTools/HelpViewer/index.html");
		return entries;
	}
}
