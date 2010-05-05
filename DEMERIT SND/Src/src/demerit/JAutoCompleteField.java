package demerit;


import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import java.awt.event.*;

/**
 * An extended JSwing text field which implements autocomplete abilities.
 */
public class JAutoCompleteField extends JTextField implements DocumentListener, ActionListener{

	private static final long serialVersionUID = -4746826571603285301L;
	Filler filler;
	JPopupMenu menu;
	Boolean noRedraw;

	public JAutoCompleteField() {
		this.filler = new Filler(new ArrayList<String>());
		this.menu = null;
		this.getDocument().addDocumentListener(this);
		this.addActionListener(new EnterAction());
		this.noRedraw = false;
	}
	
	public JAutoCompleteField(ArrayList<String> possibilities) {
		this.filler = new Filler(possibilities);
		this.menu = null;
		this.getDocument().addDocumentListener(this);
		this.addActionListener(new EnterAction());
		this.noRedraw = false;
	}
	
	public void setPossibilities(ArrayList<String> possibilities) {
		this.filler = new Filler(possibilities);
	}

	private void update() {
		String entry = this.getText();
		if(entry == null) {
			entry = "";
		}
		
		ArrayList<String> suggestions = filler.getSuggestions(entry);
		if(suggestions != null && suggestions.size() > 0 && this.hasFocus()) {
			menu = new JPopupMenu();
			for(String item : suggestions) {
				JMenuItem menuItem = new JMenuItem(item);
			    menuItem.addActionListener(this);
			    menu.add(menuItem);
			}
			Rectangle bounds = this.getBounds();
			Caret caret = this.getCaret();
			menu.show(this.getParent(), bounds.x, bounds.y + bounds.height);
			this.grabFocus();
			this.setSelectionStart(caret.getDot());
			this.setSelectionEnd(caret.getMark());
		}
		else {
			if(menu != null) {
				menu.setVisible(false);
				menu = null;
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		JMenuItem menuItem = (JMenuItem)arg0.getSource();
		this.setText(menuItem.getText());
		if(menu != null)
			menu.setVisible(false);
	}
	
	public void changedUpdate(DocumentEvent arg0) { update(); }
	public void insertUpdate(DocumentEvent arg0) { update(); }
	public void removeUpdate(DocumentEvent arg0) { update(); }

	private class EnterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(menu != null) {
				menu.setVisible(false);
				menu = null;
			}
		}
	}
	
	private class Filler {
		private ArrayList<String> possibilities;
		
		Filler(ArrayList<String> possibilities) {
			this.possibilities = possibilities;
		}
		
		ArrayList<String> getSuggestions(String source) {
			if(source.equals("")) {
				return null;
			}
			
			source = source.toLowerCase();
			ArrayList<String> suggestions = new ArrayList<String>();
			for(String possibility: possibilities) {
				if(possibility.toLowerCase().indexOf(source) != -1) {
					suggestions.add(possibility);
				}
				if(suggestions.size() == 10) {
					break;
				}
			}
			return suggestions;
		}
	}
}
