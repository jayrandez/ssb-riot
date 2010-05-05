package demerit;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * An extended button displayed in the corner of each view.
 */
public class CloseButton extends JButton implements ActionListener {

	Core core;
	View caller;
	
	public CloseButton(Core core, View caller) {
		this.core = core;
		this.caller = caller;
		addActionListener(this);
		setText("X");
	}

	public void actionPerformed(ActionEvent arg0) {
		core.stopController(caller);
	}
}
