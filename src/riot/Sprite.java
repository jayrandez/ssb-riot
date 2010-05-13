package riot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {
	public String sheet;
	public String animation;
	public int index;
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
		this.x = 0;
		this.y = 0;
		this.rotation = 0;
		this.centerX = 0;
		this.centerY = 0;
		
		this.image = null;
		
	}
	
	/* If the scene is being described for the first time its sprites are made with this */
	public Sprite(String sheet, String animation, int index, int x, int y, int rotation) {
		this.sheet = sheet;
		this.animation = animation;
		this.index = index;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.centerX = 0;
		this.centerY = 0;
		
		this.image = null;
	}
	
	/* If the scene is being interpreted and made for actual drawing its sprites are made with this */
	public Sprite(SpriteManager manager, String sheet, String animation, int index, int x, int y, int rotation) {
		this.sheet = sheet;
		this.animation = animation;
		this.index = index;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		AnimationDescriptor desc = manager.getAnimation(sheet, animation);
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;

		this.image = manager.getImage(sheet, animation, index);
		
	}
	
	public void drawOn(Graphics2D g2d) {
		int width = image.getWidth();
		int height = image.getHeight();
		g2d.drawImage(image, 0, 0, image.getWidth()-1, image.getHeight()-1, x-centerX, y-centerY, x-centerX+width-1, y-centerY+height-1, null);
	}
}
