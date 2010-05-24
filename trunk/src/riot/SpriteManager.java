package riot;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import com.thoughtworks.xstream.*;

/**
 * Class responsible for loading all spritesheets, and organizing animations and individual sprites
 * The manager will represent all sprites in all animations in all spritesheets in the given directory.
 */
public class SpriteManager {
	private ArrayList<AnimationDescriptor> animations;
	private ArrayList<ArrayList<Image>> images;
	private HashMap<String, Integer> associations;
	
	public SpriteManager(String path) {
		animations = new ArrayList<AnimationDescriptor>();
		images = new ArrayList<ArrayList<Image>>();
		associations = new HashMap<String, Integer>();
		
		/* Go through the directory "path" and locate all xml descriptors. */
		ArrayList<SpriteSheet> sheets = new ArrayList<SpriteSheet>();
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName();
			if(file.isFile() && name.contains(".xml")) {
				/* Parse a descriptor into a SpriteSheet object */
				SpriteSheet sheet = parse(file);
				if(sheet != null) {
					sheets.add(sheet);
				}
			}
		}

		/* Organize spritesheets into a more accessible format. */
		populateData(sheets, directory);
	}
	
	/**
	 * Gets the animation descriptor given the sheet name and animation name
	 */
	public AnimationDescriptor getAnimation(String sheet, String animation) {
		int index = getIndex(sheet, animation);
		return getAnimation(index);
	}
	
	/**
	 * Gets the animation descriptor given the index of the animation
	 */
	public AnimationDescriptor getAnimation(int index) {
		return animations.get(index);
	}

	/**
	 * Gets an individual sprite given a sheet name, animation name, and frame
	 */
	public Image getImage(String sheet, String animation, int frame) {
		int index = getIndex(sheet, animation);
		return getImage(index, frame);
	}
	
	/**
	 * Gets an individual sprite given an animation index and frame
	 */
	public Image getImage(int index, int frame) {
		return images.get(index).get(frame);
	}
	
	/**
	 * Gets the index of an animation given the name of the sheet and animation
	 */
	public int getIndex(String sheet, String animation) {
		Integer index = associations.get(sheet + "|" + animation);
		if(index == null)
			return 0;
		return index;
	}
	
	/**
	 * Parses a spritesheet descriptor file into a SpriteSheet object
	 */
	private SpriteSheet parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.alias("SheetDescriptor", riot.SpriteManager.SpriteSheet.class);
			parser.alias("AnimationDescriptor", riot.AnimationDescriptor.class);
			SpriteSheet sheet = (SpriteSheet)parser.fromXML(reader);
			System.out.println("Found sheet descriptor: " + descriptor.getName());
			return sheet;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Organizes all of the sprite sheet information into arraylists referenced by index
	 * This is useful because it is much quicker to pass an integer over the line than full
	 * strings for the sheet and animation name.
	 */
	private void populateData(ArrayList<SpriteSheet> sheets, File directory) {
		int index = 0;
		
		/* Go through each sprite sheet getting out each animation. */
		for(SpriteSheet sheet: sheets) {
			Image sheetImage = openImage(directory, sheet.imageFile);
			if(sheetImage == null) {
				System.out.println("Couldn't find source image " + sheet.imageFile);
			}
			
			String sheetName = sheet.sheetName;
			ArrayList<AnimationDescriptor> animationDescriptors = sheet.animations;
			
			/* Go through each animation in the sheet getting out each individual sprite. */
			for(AnimationDescriptor animation: animationDescriptors) {
				String animationName = animation.animationName;
				ArrayList<Image> frames = new ArrayList<Image>();
				for(int i = 0; i < animation.frames; i++) {
					int offsetX = animation.originX + (i*animation.width);
					int offsetY = animation.originY;
					int width = animation.width;
					int height = animation.height;
					boolean transparent = animation.transparent;
					Image subImage = getSubimage(sheetImage, offsetX, offsetY, width, height, transparent);
					frames.add(subImage);
				}
				
				/* Create an association so we can access the animation by name or index. */
				associations.put(sheetName + "|" + animationName, index);
				/* Add each animation to the arraylist and each sprite to the sprite arraylist. */
				animations.add(animation);
				images.add(frames);
				index++;
			}
		}
	}

	/**
	 * Creates an image object from the sprite sheet image file
	 */
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
	
	/**
	 * Gets an individual sprite out of the sprite sheet image
	 * The sprites are stored in graphics card memory as accelerated images for faster drawing.
	 * Will make the sprite transparent if necessary
	 */
	private Image getSubimage(Image source, int offsetX, int offsetY, int width, int height, boolean transparent) 
	{
		BufferedImage bufferedSource = (BufferedImage)source;
		BufferedImage bufferedImage = bufferedSource.getSubimage(offsetX, offsetY, width, height);
		
		/* Make the sprite transparent */
		if(transparent) {
			int transcolor = bufferedSource.getRGB(0,0);
			for (int i = 0; i < bufferedImage.getHeight(); i++)
				for (int j = 0; j < bufferedImage.getWidth(); j++)
					if (bufferedImage.getRGB(j, i) == transcolor)
						bufferedImage.setRGB(j, i, 0x8F1C1C);
		}

		/* Allocate space in the accelerated graphics memory for the sprite. */
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(), Transparency.BITMASK);
		image.getGraphics().drawImage(bufferedImage, 0, 0, null);
		
		return image;  
	}
	
	/**
	 * Class representing the data in a sprite sheet xml descriptor
	 */
	private class SpriteSheet {
		private static final long serialVersionUID = 8569167703952176877L;
		
		String sheetName;
		String imageFile;
		ArrayList<AnimationDescriptor> animations;
	}
}
