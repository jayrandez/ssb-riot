package riot;

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.extended.*;

public class FontManager {
	private HashMap<String, Integer> fontAssociations;
	private ArrayList<Font> fonts;
	private ArrayList<Color> colors;
	private ArrayList<Boolean> alignments;
	
	public FontManager(String path) {
		fontAssociations = new HashMap<String, Integer>();
		fonts = new ArrayList<Font>();
		colors = new ArrayList<Color>();
		alignments = new ArrayList<Boolean>();
		
		/* Go through the directory "path" and locate the xml descriptor. */
		FontList fontList = null;
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if(file.getName().equals("fonts.xml")) {
				fontList = parse(file);
				break;
			}
		}
		
		/* Organize descriptors into a more accessible format. */
		populateData(fontList, path);
	}
	
	public Font getFont(int index) {
		return fonts.get(index);
	}
	
	public Color getColor(int index) {
		return colors.get(index);
	}
	
	public boolean getCentered(int index) {
		return alignments.get(index);
	}
	
	public int getIndex(String fontName) {
		return fontAssociations.get(fontName);
	}

	private FontList parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.registerConverter(new ColorConverter());
			parser.alias("FontList", riot.FontManager.FontList.class);
			parser.alias("FontDescriptor", riot.FontManager.FontDescriptor.class);
			FontList desc = (FontList)parser.fromXML(reader);
			return desc;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void populateData(FontList fontList, String path) {
		int index = 0;
		for(FontDescriptor desc: fontList.fonts) {
			try {
				InputStream stream = new FileInputStream(new File(path + "/" + desc.fileName));
				Font originalFont = Font.createFont(Font.TRUETYPE_FONT, stream);
				Font font = originalFont.deriveFont((float)desc.size);
				fonts.add(font);
				colors.add(desc.color);
				alignments.add(desc.centered);
				fontAssociations.put(desc.fontName, index);
				index++;
				System.out.println("Found font descriptor: " + desc.fontName);
			}
			catch(Exception ex) {
				System.out.println("Couldn't find file: " + desc.fileName);
			}
		}
	}
	
	private class FontList {
		ArrayList<FontDescriptor> fonts;
	}
	
	private class FontDescriptor {
		String fontName;
		String fileName;
		int size;
		Color color;
		boolean centered;
	}
}
