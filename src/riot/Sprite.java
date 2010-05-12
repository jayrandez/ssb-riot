package riot;

import java.awt.image.BufferedImage;

public class Sprite {
	BufferedImage image;
	int x;
	int y;
	int rotation;
	
	public void Sprite(SpriteManager manager, String sheet, String animation, int index, int x, int y, int rotation) {
		this.image = manager.getImage(sheet, animation, index);
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public void Sprite(String text, int size, int x, int y) {
		// Create buffered image of text
	}
	
	public void drawOn(BufferedImage destination) {
		// Affine Transform Blit Here
	}
}
