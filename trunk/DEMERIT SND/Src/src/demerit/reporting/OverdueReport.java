package demerit.reporting;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import demerit.Controller;
import demerit.Core;
import demerit.View;
import demerit.models.OverdueModel;

/**
 * Controller used to generate a report showing overdue media.
 */
public class OverdueReport extends Controller {

	private File outputFile;
	
	public OverdueReport(Core core) {
		super(core);
	}

	public View init(ArrayList<Object> parameters) {
		try {
			JFileChooser jf = new JFileChooser();
			jf.setDialogTitle("Save HTML Report");
			jf.showSaveDialog(null);
			outputFile = jf.getSelectedFile();
			OverdueModel model = (OverdueModel)core.createModel("OverdueModel");
			ArrayList<String[]> records = model.getRecords();
			String assembledDocument = "";
			
			File rowFile = new File("Modules/Report/overdue_allrow.html");
			FileInputStream rowStream = new FileInputStream(rowFile);
			byte[] buffer = new byte[(int)rowFile.length()];
		    rowStream.read(buffer);
		    String rowString = new String(buffer);

			File templateFile = new File("Modules/Report/overdue.html");
			FileInputStream templateStream = new FileInputStream(templateFile);
			DataInputStream in = new DataInputStream(templateStream);
			BufferedReader templateReader = new BufferedReader(new InputStreamReader(in));
			
			String currentLine = templateReader.readLine();
			while(currentLine.indexOf("<!-- DATE -->") == -1) {
				assembledDocument += currentLine + "\n";
				currentLine = templateReader.readLine();
			}
			
			Date today = new Date();
			assembledDocument += today.toLocaleString() + "\n";
			
			while(currentLine.indexOf("<!-- ROWS -->") == -1) {
				assembledDocument += currentLine + "\n";
				currentLine = templateReader.readLine();
			}
			
			for(String[] row: records) {
				assembledDocument += assembleRow(row, rowString) + "\n";
			}
			
			while(currentLine != null) {
				assembledDocument += currentLine + "\n";
				currentLine = templateReader.readLine();
			}
			
			FileWriter writer = new FileWriter(outputFile);
			writer.write(assembledDocument);
			
			writer.close();
			templateReader.close();
			in.close();
			templateStream.close();
			rowStream.close();
			
			Core.debug("Report saved.");
			JOptionPane.showMessageDialog(null, "Report was saved succesfully.");
		}
		catch(Exception ex) {
			Core.debug("Report failed.");
			JOptionPane.showMessageDialog(null, "There was a problem generating the report.");
			ex.printStackTrace();
		}
		return null;
	}
	
	private String assembleRow(String[] data, String template) {
		int i = 0;
		while(i < data.length && template.indexOf("?") != -1) {
			template = template.replaceFirst("\\?", data[i]);
			i++;
		}
		return template;
	}

}
