package demerit;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * IMPLEMENTATION UNFINISHED
 * A class which makes it easy to generate a report given input templates.
 */
public class ReportGenerator {
	
	File descriptorFile;
	
	public ReportGenerator(String descriptorFile) {
		this.descriptorFile = new File(descriptorFile);
	}
	
	public ReportGenerator(File descriptorFile) {
		this.descriptorFile = descriptorFile;
	}
	
	public String generateReport(ArrayList<BufferedImage> images, ArrayList<ReportTable> tables, ArrayList<String> tableDescriptors) throws IllegalArgumentException {
		HashMap<String, String> replacements = new HashMap<String, String>();
		
		String original = "";
		
		try {
			Scanner scanner = new Scanner(descriptorFile);
			while(scanner.hasNextLine()) {
				original += scanner.nextLine();
			}
		}
		catch (FileNotFoundException ex) {
			throw new IllegalArgumentException();
		}
		if(tables.size() != tableDescriptors.size()) {
			throw new IllegalArgumentException();
		}
		
		try {
			for(int i = 0; i < images.size(); i++) {
				File temp = File.createTempFile(this.toString() + i, ".png");
			    ImageIO.write(images.get(i), "png", temp);
			    String html = "<img src=\"";
			    html += temp.getAbsolutePath();
			    html += "\" />";
			    replacements.put("IMAGE" + (i+1), html);
			}
		}
		catch (IOException e) {}
		
		for(int i = 0; i < tables.size(); i++) {
			ReportTable table = tables.get(i);
			String tableDescriptor = tableDescriptors.get(i);
			String html = createTableHTML(table, tableDescriptor);
			replacements.put("TABLE" + (i+1), html);
		}
		
		DynamicString replacer = new DynamicString(original, replacements);
		return replacer.getReplacement();
	}
	
	private String createTableHTML(ReportTable table, String tableDescriptor) {
		File descriptor = new File(tableDescriptor);
		String assembled = "";
		
		while(table.next()) {
			try {
				String original = "";
				Scanner scanner = new Scanner(descriptor);
				while(scanner.hasNextLine()) {
					original += scanner.nextLine();
				}
				DynamicString replacer = new DynamicString(original, table.row());
				assembled += replacer.getReplacement();
			}
			catch (FileNotFoundException ex) {
				throw new IllegalArgumentException();
			}
		}
		
		return assembled;
	}
}
