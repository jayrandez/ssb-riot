package riot;

/*
 * I RECOMMEND THAT YOU DONT LOOK AT THIS CODE IF YOU WANT TO MAINTAIN YOUR SANITY
 */

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import com.thoughtworks.xstream.XStream;

public class SpriteManager {
	private HashMap<String, HashMap<String, AnimationDescriptor>> animations;
	private HashMap<String, HashMap<String, ArrayList<BufferedImage>>> sprites;
	
	public SpriteManager(String path) {
		ArrayList<SheetDescriptor> sheets = new ArrayList<SheetDescriptor>();
		
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName();
			if(file.isFile() && name.contains(".xml")) {
				SheetDescriptor sheet = parse(file);
				if(sheet != null) {
					sheets.add(sheet);
				}
			}
		}

		animations = getAnimationsFrom(sheets);
		sprites = getSpritesFrom(sheets, directory);
	}
	
	public AnimationDescriptor getAnimation(String sheet, String animation) {
		return animations.get(sheet).get(animation);
	}

	public BufferedImage getImage(String sheet, String animation, int index) {
		return sprites.get(sheet).get(animation).get(index);
	}
	
	private SheetDescriptor parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.alias("SheetDescriptor", riot.SheetDescriptor.class);
			parser.alias("AnimationDescriptor", riot.AnimationDescriptor.class);
			SheetDescriptor sheet = (SheetDescriptor)parser.fromXML(reader);
			
			System.out.println("Found sheet descriptor: " + descriptor.getName());
			System.out.println("" + sheet);
			
			return sheet;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private HashMap<String, HashMap<String, ArrayList<BufferedImage>>> getSpritesFrom(ArrayList<SheetDescriptor> sheets, File directory) {
		HashMap<String, HashMap<String, ArrayList<BufferedImage>>> sheetMap = new HashMap<String, HashMap<String, ArrayList<BufferedImage>>>();
		for(SheetDescriptor sheet: sheets) {
			HashMap<String, ArrayList<BufferedImage>> animationMap = new HashMap<String, ArrayList<BufferedImage>>();
			Image sheetImage = openImage(directory, sheet.imageFile);
			for(AnimationDescriptor animation: sheet.animations) {
				ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();
				for(int i = 0; i < animation.frames; i++) {
					int offsetX = animation.originX + (i * animation.width);
					int offsetY = animation.originY;
					BufferedImage spriteImage = getTransparentSubimage(sheetImage, offsetX, offsetY, animation.width, animation.height);
					sprites.add(spriteImage);
				}
				animationMap.put(animation.animationName, sprites);
			}
			sheetMap.put(sheet.sheetName, animationMap);
		}
		return sheetMap;
	}

	private HashMap<String, HashMap<String, AnimationDescriptor>> getAnimationsFrom(ArrayList<SheetDescriptor> sheets) {
		HashMap<String, HashMap<String, AnimationDescriptor>> sheetMap = new HashMap<String, HashMap<String, AnimationDescriptor>>();
		for(SheetDescriptor sheet: sheets) {
			HashMap<String, AnimationDescriptor> animationMap = new HashMap<String, AnimationDescriptor>();
			for(AnimationDescriptor animation: sheet.animations) {
				animationMap.put(animation.animationName, animation);
			}
			sheetMap.put(sheet.sheetName, animationMap);
		}
		return sheetMap;
	}
	
	private Image openImage(File directory, String file) {
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File current = contents[i];
			if(current.getName().equals(file)) {
				try {
					return ImageIO.read(current);
				}
				catch (IOException e) {
					return null;
				}
			}
		}
		return null;
	}
	
	private BufferedImage getTransparentSubimage(Image source, int offsetX, int offsetY, int width, int height) {
		// DEAN MAKE THIS FUNCTION!!!
		return null;
	}
}
