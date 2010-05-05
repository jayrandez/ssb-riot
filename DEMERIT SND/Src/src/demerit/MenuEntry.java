package demerit;

import java.util.ArrayList;

/**
 * An entry to the application's menu bar which is parsed by the AppFrame class.
 */
public class MenuEntry {

	public MenuEntry(String menu, String label, String controller, ArrayList<Object> parameters, Boolean autostart, Boolean administrative) {
		this.menu = menu;
		this.label = label;
		this.controller = controller;
		this.parameters = parameters;
		this.autostart = autostart;
		this.administrative = administrative;
	}
	
	public MenuEntry(String menu, String label, String controller, Boolean autostart, Boolean administrative) {
		this.menu = menu;
		this.label = label;
		this.controller = controller;
		this.parameters = new ArrayList<Object>();
		this.autostart = autostart;
		this.administrative = administrative;
	}
	
	public String menu;
	public String label;
	public String source;
	public String controller;
	public Boolean autostart;
	public Boolean administrative;
	public ArrayList<Object> parameters;
	
}
