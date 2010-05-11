package riot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

public class SheetManager {
	ArrayList<SheetDescriptor> sheets;
	
	public SheetManager() {
		sheets = new ArrayList<SheetDescriptor>();
	}
	
	public void getSheetsFromDirectory(String path) {
		File directory = new File(path);
		if(directory.exists() && directory.isDirectory()) {
			File[] contents = directory.listFiles();
			for(int i = 0; i < contents.length; i++) {
				File file = contents[i];
				String name = file.getName();
				if(file.isFile() && name.contains(".xml")) {
					
					parse(file);
				}
			}
		}
	}
	
	private void parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.alias("SheetDescriptor", riot.SheetDescriptor.class);
			parser.alias("AnimationDescriptor", riot.AnimationDescriptor.class);
			SheetDescriptor sheet = (SheetDescriptor)parser.fromXML(reader);
			sheets.add(sheet);
			
			Riot.debug("Found sheet descriptor: " + descriptor.getName());
			Riot.debug("" + sheet);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
}
