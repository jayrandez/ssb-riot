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
import demerit.MediaData;
import demerit.View;
import demerit.models.MediaModel;
import demerit.models.OverdueModel;
import demerit.models.PopularModel;

/**
 * Controller used to generate a report of popular media.
 * Simply based on number of checkouts per book.
 */
public class PopularReport extends Controller {

	private File outputFile;
	
	public PopularReport(Core core) {
		super(core);
	}

	public View init(ArrayList<Object> parameters) {
		try {
			JFileChooser jf = new JFileChooser();
			jf.setDialogTitle("Save HTML Report");
			jf.showSaveDialog(null);
			outputFile = jf.getSelectedFile();
			PopularModel model = (PopularModel)core.createModel("PopularModel");
			MediaModel mediaModel = (MediaModel)core.createModel("MediaModel");
			ArrayList<Integer> ids = model.getPopularItems();
			
			ArrayList<String[]> records = new ArrayList<String[]>();
			for(Integer id: ids) {
				MediaData data = mediaModel.getMedia(id);
				String categories = "";
				for(String cat: data.categories){
					categories += cat + ", ";
				}
				if(!categories.equals(""))
					categories = categories.substring(0, categories.length()-2);
				String[] record = {
						data.isbn,
						data.title,
						categories
					};
				records.add(record);
			}
			
			String assembledDocument = "";
			
			File rowFile = new File("Modules/Report/popular_allrow.html");
			FileInputStream rowStream = new FileInputStream(rowFile);
			byte[] buffer = new byte[(int)rowFile.length()];
		    rowStream.read(buffer);
		    String rowString = new String(buffer);

			File templateFile = new File("Modules/Report/popular.html");
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
