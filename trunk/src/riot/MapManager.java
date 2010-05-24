package riot;

import java.io.*;
import java.util.*;
import com.thoughtworks.xstream.*;

/**
 * Class responsible for loading all map descriptors for a map's platforms
 */
public class MapManager {
	private HashMap<String, ArrayList<Rectangle>> maps;
	
	public MapManager(String path) {
		maps = new HashMap<String, ArrayList<Rectangle>>();
		
		/* Go through the directory "path" and locate all xml descriptors. */
		ArrayList<MapDescriptor> descriptors = new ArrayList<MapDescriptor>();
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName();
			if(file.isFile() && name.contains(".xml")) {
				/* Parse a descriptor into a MapDescriptor object */
				MapDescriptor desc = parse(file);
				if(desc != null) {
					descriptors.add(desc);
				}
			}
		}
		
		/* Organize descriptors into a more accessible format. */
		populateData(descriptors);
	}
	
	/**
	 * Gets the platforms in a map referenced by name
	 */
	public ArrayList<Rectangle> platformsIn(String mapName) {
		ArrayList<Rectangle> platforms = maps.get(mapName);
		if(platforms == null)
			platforms = new ArrayList<Rectangle>();
		return platforms;
	}
	
	/**
	 * Parses a map descriptor xml file into a MapDescriptor object
	 */
	private MapDescriptor parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.alias("MapDescriptor", riot.MapManager.MapDescriptor.class);
			parser.alias("Rectangle", riot.Rectangle.class);
			MapDescriptor desc = (MapDescriptor)parser.fromXML(reader);
			System.out.println("Found map descriptor: " + descriptor.getName());
			return desc;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Associates the lists of platforms with the map names
	 */
	private void populateData(ArrayList<MapDescriptor> descriptors) {
		for(MapDescriptor desc: descriptors) {
			maps.put(desc.name, desc.platforms);
		}
	}
	
	/**
	 * Class representing a map descriptor xml file
	 */
	private class MapDescriptor {
		String name;
		ArrayList<Rectangle> platforms;
	}
}
