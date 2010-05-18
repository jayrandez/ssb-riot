package riot;

/*
 * I RECOMMEND THAT YOU DONT LOOK AT THIS CODE IF YOU WANT TO MAINTAIN YOUR SANITY
 */

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import com.thoughtworks.xstream.XStream;

public class SpriteManager {
	private ArrayList<AnimationDescriptor> animations;
	private ArrayList<ArrayList<BufferedImage>> images;
	private HashMap<String, Integer> associations;
	
	public SpriteManager(String path) {
		animations = new ArrayList<AnimationDescriptor>();
		images = new ArrayList<ArrayList<BufferedImage>>();
		associations = new HashMap<String, Integer>();
		
		ArrayList<SpriteSheet> sheets = new ArrayList<SpriteSheet>();
		
		File directory = new File(path);
		File[] contents = directory.listFiles();
		for(int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName();
			if(file.isFile() && name.contains(".xml")) {
				SpriteSheet sheet = parse(file);
				if(sheet != null) {
					sheets.add(sheet);
				}
			}
		}

		populateData(sheets, directory);
	}
	
	public AnimationDescriptor getAnimation(String sheet, String animation) {
		int index = getIndex(sheet, animation);
		return getAnimation(index);
	}
	
	public AnimationDescriptor getAnimation(int index) {
		return animations.get(index);
	}

	public BufferedImage getImage(String sheet, String animation, int frame) {
		int index = getIndex(sheet, animation);
		return getImage(index, frame);
	}
	
	public BufferedImage getImage(int index, int frame) {
		return images.get(index).get(frame);
	}
	
	public int getIndex(String sheet, String animation) {
		Integer index = associations.get(sheet + "|" + animation);
		if(index == null)
			return 0;
		return index;
	}
	
	private SpriteSheet parse(File descriptor) {
		try {
			FileReader reader = new FileReader(descriptor);
			XStream parser = new XStream();
			parser.alias("SheetDescriptor", riot.SpriteManager.SpriteSheet.class);
			parser.alias("AnimationDescriptor", riot.AnimationDescriptor.class);
			SpriteSheet sheet = (SpriteSheet)parser.fromXML(reader);
			
			System.out.println("Found sheet descriptor: " + descriptor.getName());
			System.out.println("" + sheet);
			
			return sheet;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private void populateData(ArrayList<SpriteSheet> sheets, File directory) {
		int index = 0;
		
		for(SpriteSheet sheet: sheets) {
			Image sheetImage = openImage(directory, sheet.imageFile);
			String sheetName = sheet.sheetName;
			ArrayList<AnimationDescriptor> animationDescriptors = sheet.animations;
			
			for(AnimationDescriptor animation: animationDescriptors) {
				String animationName = animation.animationName;
				ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
				
				for(int i = 0; i < animation.frames; i++) {
					int offsetX = animation.originX + (i*animation.width);
					int offsetY = animation.originY;
					int width = animation.width;
					int height = animation.height;
					boolean transparent = animation.transparent;
					BufferedImage subImage = getSubimage(sheetImage, offsetX, offsetY, width, height, transparent);
					frames.add(subImage);
				}
				
				associations.put(sheetName + "|" + animationName, index);
				animations.add(animation);
				images.add(frames);
				index++;
			}
		}
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
	
	private BufferedImage getSubimage(Image source, int offsetX, int offsetY, int width, int height, boolean transparent) 
	{
		BufferedImage bufferedSource = (BufferedImage)source;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		image = bufferedSource.getSubimage(offsetX, offsetY, width, height);
		int transcolor = image.getRGB(0,0);
		
		for (int i = 0; i < image.getHeight(); i++)
		{
			for (int j = 0; j < image.getWidth(); j++)
			{
				if (image.getRGB(j, i) == transcolor)
					image.setRGB(j, i, 0x8F1C1C);
			}
		}  

		return image;  
	}
	
	private class SpriteSheet implements Serializable {
		private static final long serialVersionUID = 8569167703952176877L;
		
		String sheetName;
		String imageFile;
		ArrayList<AnimationDescriptor> animations;
		
		public String toString() {
			return " > " + animations.toString();
		}
	}

}
