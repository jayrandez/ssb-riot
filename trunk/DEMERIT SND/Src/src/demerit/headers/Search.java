package demerit.headers;

import java.util.ArrayList;
import java.util.HashMap;

import demerit.Header;
import demerit.MenuEntry;

/**
 * The search module contains a single controller used to search for media.
 */
public class Search extends Header {

	public ArrayList<MenuEntry> getMenuEntries() {
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		entries.add(new MenuEntry("Tools", "Search Media", "Search", false, false));
		return entries;
	}

	public HashMap<String, String> getHelpEntries() {
		HashMap<String, String> entries = new HashMap<String, String>();
		entries.put("Search", "Modules/Search/Search/index.html");
		return entries;
	}
}
