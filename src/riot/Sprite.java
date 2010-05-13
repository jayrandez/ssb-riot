package riot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Sprite {
	public String sheet;
	public String animation;
	public int index;
	public int x;
	public int y;
	public int rotation;
	public BufferedImage image;
	
	/* If the scene is being described for the first time its sprites are made with this */
	public Sprite(String sheet, String animation, int index, int x, int y, int rotation) {
		this.sheet = sheet;
		this.animation = animation;
		this.index = index;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	/* If the scene is being interpreted and made for actual drawing its sprites are made with this */
	public Sprite(SpriteManager manager, String sheet, String animation, int index, int x, int y, int rotation) {
		this.sheet = sheet;
		this.animation = animation;
		this.index = index;
		this.x = x;
		this.y = y;
		this.rotation = rotation;

		this.image = manager.getImage(sheet, animation, index);
	}
	
	/* If the scene is being interprreted and made for actual drawing the text is made with this */
	public Sprite(String text, int size, int x, int y) {
		// Create buffered image of text
	}
	
	public void drawOn(Graphics2D g2d) {
		// Affine Transform Blit Here
	}
}
