package demerit;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * The class representing the application's window.
 * Manages the tabs and allows new tabs to be loaded in function calls.
 * Can create a menu bar from an array list
 */
public class AppFrame extends JFrame implements ChangeListener, ActionListener {
	private static final long serialVersionUID = 1104127095375558087L;
	
	Core core;
	HashMap<String, ArrayList<MenuEntry>> menus;
	JMenuBar menuBar;
	JTabbedPane tabPane;
	
	public AppFrame(Core core) {
		this.core = core;
		menus = new HashMap<String, ArrayList<MenuEntry>>();
		
		tabPane = new JTabbedPane();
		tabPane.setBorder(new EmptyBorder(new Insets(15,5,5,5)));
		tabPane.addChangeListener(this);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(tabPane);
		this.add(container);
		
		this.setSize(1024, 768);
		this.setVisible(true);
	} 
	
	public void createMenuBar(HashMap<String, ArrayList<MenuEntry>> menus) {
		this.menus = menus;
		menuBar = new JMenuBar();
		
		ArrayList<MenuEntry> helpMenu = menus.get("Help");
		ArrayList<MenuEntry> fileMenu = menus.get(Core.applicationName);
		JMenu newMenu;
		
		newMenu = menuFor(Core.applicationName, fileMenu);
		if(newMenu != null)
			menuBar.add(newMenu);
		
		for(String menuTitle: menus.keySet()) {
			if(!menuTitle.equals(Core.applicationName) && !menuTitle.equals("Help")) {
				ArrayList<MenuEntry> menuEntries = menus.get(menuTitle);
				newMenu = menuFor(menuTitle, menuEntries);
				if(newMenu != null)
					menuBar.add(newMenu);
			}
		}
		
		newMenu = menuFor("Help", helpMenu);
		if(newMenu != null)
			menuBar.add(newMenu);
		
		this.setJMenuBar(menuBar);
	}
	
	public JMenu menuFor(String menuTitle, ArrayList<MenuEntry> menuEntries) {
		if(menuEntries != null && menuTitle != null) {
			menuEntries = new ArrayList<MenuEntry>(menuEntries);
			JMenu menu = new JMenu(menuTitle);	
			String currentModule = null;
			while(menuEntries.size() > 0) {
				MenuEntry currentEntry = menuEntries.get(0);
				if(currentEntry.source != currentModule && currentModule != null) {
					menu.add(new JSeparator());
				}
				JMenuItem menuItem = new JMenuItem(menuEntries.get(0).label);
				currentModule = currentEntry.source;
				menuEntries.remove(0);
				menuItem.addActionListener(this);
				menu.add(menuItem);
			}
			return menu;
		}
		else {
			return null;
		}
	}
	
	public void addView(View view, String title) {
		tabPane.add(view, title);
		tabPane.setSelectedIndex(tabPane.getTabCount()-1);
		updateWindowTitle();
	}
	
	public void removeView(View view) {
		Integer index = null;
		for(Integer i = 0; i < tabPane.getTabCount(); i++) {
			if(tabPane.getComponent(i) == view) {
				index = i;
				break;
			}
		}
		if(index != null) {
			tabPane.remove(index);
		}
		updateWindowTitle();
	}
	
	private void updateWindowTitle() {
		if(tabPane.getTabCount() == 0) {
			this.setTitle(Core.applicationName);
		}
		else {
			String title = tabPane.getTitleAt(tabPane.getSelectedIndex());
			this.setTitle(Core.applicationName + " - " + title);
		}
	}

	public void stateChanged(ChangeEvent e) {
		updateWindowTitle();
	}

	public void actionPerformed(ActionEvent a) {
		JMenuItem selection = (JMenuItem)a.getSource();
		String label = selection.getText();
		Core.debug("Menu click on: " + label);
		for(ArrayList<MenuEntry> menu: menus.values()) {
			Core.debug("existant");
			for(MenuEntry entry: menu) {
				Core.debug("here");
				if(entry.label.equals(label)) {
					Core.debug("Menu item clicked.");
					core.menuItemClicked(this, entry);
					return;
				}
			}
		}
	}
}
