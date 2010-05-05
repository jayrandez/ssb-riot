package demerit;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

/**
 * A JSwing label which is extended to function like a browser-style hyperlink
 */
public class JLinkLabel extends JLabel implements MouseListener, ButtonModel {
	private static final long serialVersionUID = 96965649941873819L;
	
	Boolean hovering;
	ActionListener action;
	Color previous;
	
	public JLinkLabel() {
		super("");
		hovering = false;
		previous = this.getForeground();
		this.addMouseListener(this);
	}

	public JLinkLabel(String text) {
		super(text);
		hovering = false;
		previous = this.getForeground();
		this.addMouseListener(this);
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		if(hovering) {
			g.setColor(Color.blue);
		}
		else {
			g.setColor(previous);
		}
		
		int x = (int)(g.getClipBounds().getX());
		int y = (int)(g.getClipBounds().getY() + g.getClipBounds().getHeight() - 2);
		int width = this.getWidth();
		
		g.drawLine(x, y, x+width, y);
		if(this.getFont().getSize() >= 20) {
			g.drawLine(x, y+1, x+width, y+1);
		}
	}
	
	public void addActionListener(ActionListener act) {
		action = act;
	}
	
	public void mouseClicked(MouseEvent arg0) {
		if(action != null) {
			ActionEvent e = new ActionEvent(this, 0, "Link clicked.");
			action.actionPerformed(e);
		}
	}

	public void mouseEntered(MouseEvent arg0) {
		previous = this.getForeground();
		this.setForeground(Color.blue);
		hovering = true;
		this.repaint();
	}

	public void mouseExited(MouseEvent arg0) {
		this.setForeground(previous);
		hovering = false;
		this.repaint();
	}

	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void addChangeListener(ChangeListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addItemListener(ItemListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getActionCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMnemonic() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isArmed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRollover() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeActionListener(ActionListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeChangeListener(ChangeListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItemListener(ItemListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActionCommand(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setArmed(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGroup(ButtonGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMnemonic(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPressed(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRollover(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelected(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}
}
