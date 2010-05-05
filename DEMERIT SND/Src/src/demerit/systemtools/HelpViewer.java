package demerit.systemtools;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JTextPane;

import org.swixml.SwingEngine;

import demerit.Controller;
import demerit.Core;
import demerit.View;

/**
 * A controller used to locate the help file for specific controllers and view them.
 */
public class HelpViewer extends Controller {
	
	private View view;

	private SwingEngine _body;
	private SwingEngine _mode;
	HashMap<String, EventListener> handlers;
	HashMap<String, String> files;
	
	public HelpViewer(Core core) {
		super(core);
	}

	public View init(ArrayList<Object> parameters) {
		view = new View(core);
		handlers = new HashMap<String, EventListener>();
		files = core.requestHelpFiles(this);
		
		handlers.put("cbbHelp", new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				JComboBox select = (JComboBox)_mode.find("cbbHelp");
				JTextPane viewer = (JTextPane)_body.find("tpnViewer");
				String filePath = files.get(select.getSelectedItem());
				try {
					URL url = new File(filePath).toURL();
					viewer.setPage(url);
				} catch (Exception e) {}
				
			}
		});
		
		_mode = view.defineRegion(View.MODE, "Modules/SystemTools/HelpViewer/mode.xml", handlers);
		_body = view.defineRegion(View.BODY, "Modules/SystemTools/HelpViewer/body.xml", handlers);
		
		JComboBox select = (JComboBox)_mode.find("cbbHelp");
		fillComboBox();
		select.setSelectedItem("About Demerit");
		
		return view;
	}
	
	private void fillComboBox() {
		JComboBox select = (JComboBox)_mode.find("cbbHelp");
		for(String name: files.keySet()) {
			select.addItem(name);
		}
	}

}
