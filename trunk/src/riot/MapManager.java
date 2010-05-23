package riot;

import java.io.*;
import java.util.*;

import com.thoughtworks.xstream.XStream;

public class MapManager {

	private HashMap<String, ArrayList<Rectangle>> maps;
	
	public MapManager(String path) {
		maps = new HashMap<String, ArrayList<Rectangle>>();
		
		ArrayList<MapDescriptor> descriptors = new ArrayList<MapDescriptor>();
		
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName();
			if(file.isFile() && name.contains(".xml")) {
				MapDescriptor desc = parse(file);
				if(desc != null) {
					descriptors.add(desc);
				}
			}
		}

		populateData(descriptors);
	}

	public ArrayList<Rectangle> platformsIn(String mapName) {
		ArrayList<Rectangle> platforms = maps.get(mapName);
		if(platforms == null)
			platforms = new ArrayList<Rectangle>();
		return platforms;
	}
	
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
	
	private void populateData(ArrayList<MapDescriptor> descriptors) {
		for(MapDescriptor desc: descriptors) {
			maps.put(desc.name, desc.platforms);
		}
	}
	
	
	private class MapDescriptor {
		String name;
		ArrayList<Rectangle> platforms;
	}
}
