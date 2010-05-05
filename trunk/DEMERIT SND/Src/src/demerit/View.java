package demerit;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.swixml.SwingEngine;

/**
 * Class representing the view aspect of the MVC architecture.
 * Views display the user interface to the user.
 * Is not subclassed, rather uses XML descriptors to describe tailored appearance
 * of various controllers.
 */
public class View extends JPanel {
	private static final long serialVersionUID = 3824654298641127126L;
	
	private static final int padding = 7;
	
	public static final Integer MODE = 0;
	public static final Integer HEADER = 1;
	public static final Integer SWITCH = 2;
	public static final Integer BODY = 3;
	public static final Integer SIDE = 4;
	public static final Integer ACTIONS = 5;
	public static final Integer DEFAULT = 6;
	
	Core core;
	JButton close;
	ArrayList<JPanel> panels;
	
	public View(Core core) {
		this.core = core;
		
		// Create the JPanels which a controller can set.
		setLayout(new BorderLayout());
		panels = new ArrayList<JPanel>();
		for(int i = 0; i < 7; i++) {
			JPanel insert = new JPanel(new BorderLayout());
			insert.setBorder(new EmptyBorder(padding, padding, padding, padding));
			panels.add(insert);
		}
		
		// Magic for top portion of a generic View.
		JPanel top = new JPanel(new BorderLayout());
		JPanel topTop = new JPanel(new BorderLayout());
		JPanel topTopLeft = panels.get(MODE);
		JPanel topTopRight = new JPanel(); // Close Button
		topTopRight.setBorder(new EmptyBorder(padding, padding, padding, padding));
		JPanel topMiddle = panels.get(HEADER);
		topTop.add(topTopLeft, BorderLayout.WEST);
		topTop.add(topTopRight, BorderLayout.EAST);
		top.add(topTop, BorderLayout.NORTH);
		top.add(topMiddle, BorderLayout.CENTER);
		
		// Magic for middle portion of a generic View.
		JPanel middle = new JPanel(new BorderLayout());
		JPanel middleLeft = new JPanel(new BorderLayout());
		JPanel middleLeftTop = new JPanel(new BorderLayout());
		JPanel middleLeftTopLeft = panels.get(SWITCH);
		JPanel middleLeftMiddle = panels.get(BODY);
		JPanel middleRight = panels.get(SIDE);
		middleLeftTop.add(middleLeftTopLeft, BorderLayout.WEST);
		middleLeft.add(middleLeftTop, BorderLayout.NORTH);
		middleLeft.add(middleLeftMiddle, BorderLayout.CENTER);
		middle.add(middleLeft, BorderLayout.CENTER);
		middle.add(middleRight, BorderLayout.EAST);
		
		// Magic for lower portion of a generic View.
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bottomLeft = panels.get(ACTIONS);
		JPanel bottomRight = panels.get(DEFAULT);
		bottom.add(bottomLeft, BorderLayout.WEST);
		bottom.add(bottomRight, BorderLayout.EAST);
		
		// Color specific regions.
		top.setBackground(Color.GRAY);
		topTop.setBackground(Color.GRAY);
		topTopLeft.setBackground(Color.GRAY);
		topTopRight.setBackground(Color.GRAY);
		topMiddle.setBackground(Color.GRAY);
		middleRight.setBackground(Color.LIGHT_GRAY);
		bottom.setBackground(Color.GRAY);
		bottomLeft.setBackground(Color.GRAY);
		bottomRight.setBackground(Color.GRAY);
		
		// Allow view to close itself.
		close = new CloseButton(core, this);
		topTopRight.add(close);
		
		// Add each section to this JPanel.
		add(top, BorderLayout.NORTH);
		add(middle, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		// Make unused panels invisible.
		hideUnused();
		
	}
	
	public SwingEngine defineRegion(Integer region, File regionDescriptor, HashMap<String, EventListener> actions) {
		SwingEngine swix = null;
		
		try {
			if(region < 0 || region > 6) {
				Exception ex = new Exception("That is not a valid view region.");
				throw ex;
			}
			Font my_font = new Font("Tahoma", 0, 30);
			swix = new SwingEngine(this);
			swix.getTaglib().registerTag("autocomplete", JAutoCompleteField.class);
			swix.getTaglib().registerTag("autocompletescroll", JAutoCompleteScroll.class);
			swix.getTaglib().registerTag("mediatable", JMediaTable.class);
			swix.getTaglib().registerTag("linklabel", JLinkLabel.class);
			panels.get(region).add(swix.render(regionDescriptor));
			
			/* This would be so much simpler in a language with duck typing. Like Python! */
			if(actions != null) {
				for(String id: actions.keySet()) {
					Component component = swix.find(id);
					EventListener action = actions.get(id);
					if(component != null) {
						if(action instanceof ActionListener) {
							if(component instanceof JMediaTable) {
								((JMediaTable)component).addActionListener((ActionListener)action);
							}
							else if(!swix.setActionListener(component, (ActionListener)action)) {
								if(ButtonModel.class.isAssignableFrom(component.getClass())) {
									((ButtonModel)component).addActionListener((ActionListener)action);
								}
								else if(JTextField.class.isAssignableFrom(component.getClass())) {
									((JTextField)component).addActionListener((ActionListener)action);
								}
							}
						}
						else if(action instanceof ItemListener) {
							if(JComboBox.class.isAssignableFrom(component.getClass())) {
								((JComboBox)component).addItemListener((ItemListener)action);
							}
						}
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Core.debug("SwiX rendering failed for descriptor: ");
			Core.debug(regionDescriptor.toString());
		}
		
		hideUnused();
		return swix;
	}
	
	public void undefineRegion(Integer region) {
		if(region >= 0 && region < 7) {
			JPanel panel = panels.get(region);
			panel.removeAll();
			hideUnused();
		}
	}
	
	public SwingEngine defineRegion(Integer region, String regionDescriptor, HashMap<String, EventListener> actions) {
		File descriptorFile = new File(regionDescriptor);
		return defineRegion(region, descriptorFile, actions);
	}
	
	private void hideUnused() {
		for(Integer i = 0; i < 7; i++) {
			JPanel panel = panels.get(i);
			if(panel.getComponentCount() == 0) {
				panel.setVisible(false);
			}
			else {
				panel.setVisible(true);
			}
		}
	}
}
