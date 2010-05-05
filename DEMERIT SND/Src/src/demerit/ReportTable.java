package demerit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * IMPLEMENTATION NOT FINISHED
 */
public class ReportTable {

	private ArrayList<String> rowKeys;
	private ArrayList<String[]> rows;
	private Integer nextRow;
	
	public ReportTable(ArrayList<String> rowKeys) {
		this.rowKeys = rowKeys;
		this.nextRow = 0;
	}
	
	public void appendRow(String[] data) throws IllegalArgumentException {
		if(data.length == rowKeys.size())
			rows.add(data);
		else
			throw new IllegalArgumentException();
	}
	
	public Boolean next() {
		if(nextRow < rows.size()) {
			return false;
		}
		else {
			nextRow++;
			return true;
		}
	}
	
	public HashMap<String, String> row() {
		String[] current = rows.get(nextRow-1);
		HashMap<String, String> assembled = new HashMap<String, String>();
		for(int i = 0; i < rows.size(); i++) {
			assembled.put(rowKeys.get(i), current[i]);
		}
		return assembled;
	}
}
