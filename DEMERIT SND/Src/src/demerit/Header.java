package demerit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The module header superclass for a module which will be dynamically loaded.
 * Allows core to receive information regarding menu entries and help files.
 */
public abstract class Header {

	abstract public ArrayList<MenuEntry> getMenuEntries();
	abstract public HashMap<String, String> getHelpEntries();

}
