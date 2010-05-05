package demerit.headers;

import java.util.ArrayList;
import java.util.HashMap;

import demerit.Header;
import demerit.MenuEntry;

/**
 * The reporting class contains all the controllers which connect to the database
 * to generate reports.
 */
public class Reporting extends Header {

	public ArrayList<MenuEntry> getMenuEntries() {
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		entries.add(new MenuEntry("Reporting", "Overdue Media", "OverdueReport", false, false));
		entries.add(new MenuEntry("Reporting", "Popular Media", "PopularReport", false, false));
		return entries;
	}

	public HashMap<String, String> getHelpEntries() {
		return null;
	}
}
