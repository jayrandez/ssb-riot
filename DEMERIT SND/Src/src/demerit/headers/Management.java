package demerit.headers;

import java.util.ArrayList;
import java.util.HashMap;

import demerit.Header;
import demerit.MenuEntry;

/**
 * The management class contains all of the controllers used to modify the media
 * and patron descriptive information in the database.
 */
public class Management extends Header {

	public ArrayList<MenuEntry> getMenuEntries() {
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		entries.add(new MenuEntry("Tools", "Add Media", "MediaInsert", false, true));
		entries.add(new MenuEntry("Tools", "View Media", "MediaView", false, false));
		entries.add(new MenuEntry("Tools", "New Account", "PatronInsert", false, false));
		entries.add(new MenuEntry("Tools", "Account Information", "PatronView", false, false));
		return entries;
	}

	public HashMap<String, String> getHelpEntries() {
		HashMap<String, String> entries = new HashMap<String, String>();
		entries.put("New Account", "Modules/Management/PatronInsert/index.html");
		entries.put("Add Media", "Modules/Management/MediaInsert/index.html");
		entries.put("Account Info", "Modules/Management/PatronView/index.html");
		entries.put("Media Info", "Modules/Management/MediaView/index.html");
		return entries;
	}
}
