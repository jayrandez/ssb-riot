package riot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {
	public String sheet;
	public String animation;
	public int index;
	public int frame;
	
	public int x;
	public int y;
	public int centerX;
	public int centerY;
	public int rotation;
	
	public BufferedImage image;
	
	public Sprite() {
		this.sheet = "";
		this.animation = "";
		this.index = 0;
		
		this.frame = 0;
		this.x = 0;
		this.y = 0;
		this.rotation = 0;
		this.centerX = 0;
		this.centerY = 0;
		
		this.image = null;
		
	}
	
	public Sprite(SpriteManager manager, String sheet, String animation, int frame, int x, int y, int rotation) {
		this.sheet = sheet;
		this.animation = animation;
		this.index = manager.getIndex(sheet, animation);
		
		this.frame = frame;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		AnimationDescriptor desc = manager.getAnimation(sheet, animation);
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;

		this.image = manager.getImage(sheet, animation, frame);
		
	}
	
	public Sprite(SpriteManager manager, int index, int frame, int x, int y, int rotation) {
		this.index = index;
		
		this.frame = frame;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		AnimationDescriptor desc = manager.getAnimation(index);
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;

		this.image = manager.getImage(index, frame);
		
	}
	
	public void drawOn(Graphics2D g2d) {
		int width = image.getWidth();
		int height = image.getHeight();
		g2d.drawImage(image, x-centerX, y-centerY, null);
	}
}
