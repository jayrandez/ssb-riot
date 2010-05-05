package demerit;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * An extended scroll pane which contains a media table specifically designed to:
 * display all information available from a media descriptor,
 * add or remove fields / header dynamically,
 * return a list of fields which have been check boxed.
 */
public class JMediaTable extends JScrollPane implements ButtonModel {
	private static final long serialVersionUID = 9047553657917843116L;
	
	public static final int ID = 0;
	public static final int TYPE = 1;
	public static final int FORMAT = 2;
	public static final int TITLE = 3;
	public static final int LENGTH = 4;
	public static final int CALLNUMBER = 5;
	public static final int BARCODE = 6;
	public static final int COPYRIGHT = 7;
	public static final int DESCRIPTION = 8;
	public static final int EDITION = 9;
	public static final int GENRE = 10;
	public static final int ISBN = 11;
	public static final int PRODUCER = 12;
	public static final int ACTORS = 13;
	public static final int AUTHORS = 14;
	public static final int CATEGORIES = 15;
	public static final int SELECTED = 16;
	public static final int STATUS = 17;
	
	public static final String[] fieldTypeStrings = {
		"DBID",
		"Type",
		"Format",
		"Title",
		"Length",
		"Call #",
		"Barcode",
		"Copyright",
		"Description",
		"Edition",
		"Genre",
		"ISBN",
		"Producer",
		"Actor",
		"Author",
		"Category",
		"",
		"Status"
	};
	
	JTable table;
	MediaTableModel model;
	ArrayList<MediaData> rows;
	ArrayList<Integer> fields;
	Font currentFont;
	ArrayList<ActionListener> actions;
	
	public JMediaTable() {
		actions = new ArrayList<ActionListener>();
		fields = new ArrayList<Integer>();
		fields.add(JMediaTable.SELECTED);
		fields.add(JMediaTable.TYPE);
		fields.add(JMediaTable.FORMAT);
		fields.add(JMediaTable.TITLE);
		fields.add(JMediaTable.AUTHORS);
		fields.add(JMediaTable.CALLNUMBER);
		currentFont = new Font("Arial", 0, 18);
		displayFields(fields);
	}
	
	public JMediaTable(ArrayList<Integer> fields) {
		currentFont = new Font("Arial", 0, 20);
		displayFields(fields);
	}

	private class Adapter extends MouseAdapter { 
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2){
				Core.debug("Table double-clicked.");
				for(ActionListener action: actions) {
					int[] selected = table.getSelectedRows();
					if(selected.length == 1) {
						ActionEvent ev = new ActionEvent(rows.get(selected[0]), 0, "Table Clicked");
						action.actionPerformed(ev);
					}
				}
	      	}
	     }
	}
	
	public void hideHeader() {
		table.setTableHeader(null);
	}
	
	public void paint(Graphics g) {
		FontMetrics metrics = this.getGraphics().getFontMetrics(currentFont);
		table.setRowHeight(metrics.getHeight()+6);
		super.paint(g);
	}
	
	public void setFont(Font font) {
		currentFont = font;
	}
	
	public void addItem(MediaData item) {
		rows.add(item);
		this.repaint();
	}
	
	public void removeItem(MediaData item) {
		rows.remove(item);
		this.repaint();
	}
	
	public void removeAllItems() {
		rows = new ArrayList<MediaData>();
		this.repaint();
	}
	
	public ArrayList<MediaData> removeSelected() {
		ArrayList<MediaData> removable = new ArrayList<MediaData>();
		for(MediaData row: rows) {
			if(row.selected == true) {
				removable.add(row);
			}
		}
		for(MediaData remove: removable) {
			rows.remove(remove);
		}
		this.repaint();
		return removable;
	}
	
	public void displayFields(ArrayList<Integer> fields) {
		this.fields = fields;
		model = new MediaTableModel();
		table = new JTable(model);
		this.setViewportView(table);
		rows = new ArrayList<MediaData>();
		table.setDefaultRenderer(String.class, new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
				JLabel label = new JLabel((String)value);
				label.setFont(currentFont);
				JPanel panel = new JPanel(new FlowLayout());
				panel.add(label);
				panel.setBackground(Color.white);
				return panel;
			}
		});
		table.addMouseListener(new Adapter());
		for(int i = 0; i < fields.size(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			int fieldType = fields.get(i);
			switch(fieldType) {
				case SELECTED:
					column.setPreferredWidth(50);
					break;
				case TYPE:
					column.setPreferredWidth(150);
					break;
				case FORMAT:
					column.setPreferredWidth(150);
					break;
				case TITLE:
					column.setPreferredWidth(600);
					break;
				case AUTHORS:
					column.setPreferredWidth(355);
					break;
				case CALLNUMBER:
					column.setPreferredWidth(180);
					break;
				case STATUS:
					column.setPreferredWidth(160);
					break;
				default:
					column.setPreferredWidth(150);
					break;
			}
		}
	}
	
	public ArrayList<MediaData> getItems() {
		return rows;
	}
	
	private class MediaTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 4354029595520983383L;

		public String getColumnName(int col) {
			return fieldTypeStrings[fields.get(col)];
		}

		public int getColumnCount() {
			return fields.size();
		}

		public int getRowCount() {
			return rows.size();
		}

		public Object getValueAt(int row, int col) {
			MediaData desc = rows.get(row);
			switch(fields.get(col)) {
				case ID:
					return makeLabel(desc.id.toString());
				case TYPE:
					return makeLabel(MediaData.typeValues[desc.type]);
				case FORMAT:
					return makeLabel(MediaData.formatValues[desc.format]);
				case TITLE:
					return makeLabel(desc.title);
				case LENGTH:
					return makeLabel(desc.length);
				case CALLNUMBER:
					return makeLabel(desc.callNumber);
				case BARCODE:
					return makeLabel(desc.barcode);
				case COPYRIGHT:
					return makeLabel(desc.copyright);
				case DESCRIPTION:
					return makeLabel(desc.description);
				case EDITION:
					return makeLabel(desc.edition);
				case GENRE:
					return makeLabel(MediaData.genreValues[desc.genre]);
				case ISBN:
					return makeLabel(desc.isbn);
				case STATUS:
					return makeLabel(desc.status);
				case PRODUCER:
					if(desc.actors.size() > 0)
						return makeLabel(desc.producers.get(0));
					else
						return makeLabel("");
				case ACTORS:
					if(desc.actors.size() > 0)
						return makeLabel(desc.actors.get(0));
					else
						return makeLabel("");
				case AUTHORS:
					if(desc.authors.size() > 0)
						return makeLabel(desc.authors.get(0));
					else
						return makeLabel("");
				case CATEGORIES:
					if(desc.categories.size() > 0)
						return makeLabel(desc.categories.get(0));
					else
						return makeLabel("");
				case SELECTED:
					return desc.selected;
				default:
					return null;
			}
		}
		
		public String makeLabel(String value) {
			return value;
		}
		
		public Class<?> getColumnClass(int col) {
			if(col >= fields.size()) {
				return String.class;
			}
			if(fields.get(col) == SELECTED) {
				return Boolean.class;
			}
			else {
				return String.class;
			}
		}
		
		public boolean isCellEditable(int row, int col) {
			if(fields.get(col) == SELECTED) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public void setValueAt(Object value, int row, int col) {
	        if(fields.get(col) == SELECTED) {
	        	rows.get(row).selected = (Boolean)value;
	        }
	        fireTableCellUpdated(row, col);
	    }

	}

	public void addActionListener(ActionListener arg0) {
		Core.debug("adding action listener");
		actions.add(arg0);
	}
	public void addChangeListener(ChangeListener arg0) {
	}
	public void addItemListener(ItemListener arg0) {
	}
	public String getActionCommand() {
		return null;
	}
	public int getMnemonic() {
		return 0;
	}
	public boolean isArmed() {
		return false;
	}
	public boolean isPressed() {
		return false;
	}
	public boolean isRollover() {
		return false;
	}
	public boolean isSelected() {
		return false;
	}
	public void removeActionListener(ActionListener arg0) {
	}
	public void removeChangeListener(ChangeListener arg0) {
	}
	public void removeItemListener(ItemListener arg0) {
	}
	public void setActionCommand(String arg0) {
	}
	public void setArmed(boolean arg0) {
	}
	public void setGroup(ButtonGroup arg0) {
	}
	public void setMnemonic(int arg0) {
	}
	public void setPressed(boolean arg0) {
	}
	public void setRollover(boolean arg0) {
	}
	public void setSelected(boolean arg0) {
	}
	public Object[] getSelectedObjects() {
		return null;
	}
}
