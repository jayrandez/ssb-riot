package demerit;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * An extended JSwing scroll pane which includes multiple auto complete fields
 * and which has the ability to add/remove/display multiple fields.
 */
public class JAutoCompleteScroll extends JScrollPane implements ActionListener, AdjustmentListener {

	private static final long serialVersionUID = 6580914664771985844L;
	JPanel internal;
	JPanel linkRow;
	ArrayList<String> possibilities;
	
	public JAutoCompleteScroll() {
		this.internal = new JPanel();
		this.setViewportView(internal);
		this.possibilities = new ArrayList<String>();
		
		linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLinkLabel link = new JLinkLabel("add another");
		link.addActionListener(this);
		linkRow.add(link);
		
		//this.setBorder(new EmptyBorder(0,0,0,0));
		this.getVerticalScrollBar().addAdjustmentListener(this);
		internal.setLayout(new GridLayout(0,1,-5,-5));
		internal.add(linkRow);
		addRow();
	}
	
	public JAutoCompleteScroll(ArrayList<String> possibilities) {
		this.internal = new JPanel();
		this.setViewportView(internal);
		this.possibilities = possibilities;
		
		linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLinkLabel link = new JLinkLabel("add another");
		link.addActionListener(this);
		linkRow.add(link);
		
		//this.setBorder(new EmptyBorder(0,0,0,0));
		internal.setLayout(new GridLayout(0,1,-5,-5));
		internal.add(linkRow);
		addRow();
	}
	
	public void setPossibilities(ArrayList<String> possibilities) {
		this.possibilities = possibilities;
		for(int i = 0; i < internal.getComponentCount()-1; i++) {
			JPanel row = (JPanel)internal.getComponent(i);
			JAutoCompleteField field = (JAutoCompleteField)row.getComponent(0);
			field.setPossibilities(possibilities);
		}
	}
	
	public ArrayList<String> getValues() {
		ArrayList<String> values = new ArrayList<String>();
		Component[] components = internal.getComponents();
		for(Component component : components) {
			Class a = (((JPanel)component).getComponents()[0]).getClass();
			if(a.isAssignableFrom(JAutoCompleteField.class)) {
				JAutoCompleteField b = (JAutoCompleteField)((JPanel)component).getComponents()[0];
				values.add(b.getText());
			}
		}
		return values;
	}
	
	public void clearFields() {
		internal.removeAll();
		addRow();
	}
	
	public void clearAll() {
		internal.removeAll();
	}
	
	public void addRow(String content) {
		internal.remove(linkRow);
		
		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JAutoCompleteField field = new JAutoCompleteField(possibilities);
		field.setPreferredSize(new Dimension(200,25));
		field.setText(content);
		field.grabFocus();
		row.add(field);
		JLinkLabel remove = new JLinkLabel("remove");
		remove.addActionListener(new RowListener(row));
		row.add(remove);
		internal.add(row);
		internal.add(linkRow);
	}
	
	public void addRow() {
		addRow("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		addRow();
		this.setVisible(false);
		this.setVisible(true);
	}
	
	private class RowListener implements ActionListener {
		JPanel row;
		public RowListener(JPanel row) {
			this.row = row;
		}
		public void actionPerformed(ActionEvent e) {
			JPanel parent = (JPanel) row.getParent();
			parent.remove(row);
			parent.validate();
			parent.getParent().validate();
		}
		
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		internal.repaint();
	}
}
