package demerit.headers;

import java.util.ArrayList;
import java.util.HashMap;

import demerit.Header;
import demerit.MenuEntry;

/**
 * Contains controllers for check in and checkout.
 */
public class Transaction extends Header {

	public ArrayList<MenuEntry> getMenuEntries() {
		ArrayList<MenuEntry> entries = new ArrayList<MenuEntry>();
		entries.add(new MenuEntry("Tools", "Check Out", "CheckOut", true, false));
		entries.add(new MenuEntry("Tools", "Check In", "CheckIn", false, false));
		return entries;
	}

	public HashMap<String, String> getHelpEntries() {
		HashMap<String, String> entries = new HashMap<String, String>();
		entries.put("Check In", "Modules/Transaction/CheckIn/index.html");
		entries.put("Check Out", "Modules/Transaction/CheckOut/index.html");
		return entries;
	}
}