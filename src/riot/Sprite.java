package riot;

import java.awt.Graphics;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Sprite {
	public int index;
	public int frame;
	public int x;
	public int y;
	public int rotation;
	public boolean flipped;
	public int centerX;
	public int centerY;
	public int width;
	public int height;
	public Image image;
	
	public Sprite(SpriteManager manager, int index, int frame, int x, int y, int rotation, boolean flipped) {
		AnimationDescriptor desc = manager.getAnimation(index);
		this.index = index;
		this.frame = frame;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.flipped = flipped;
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;
		this.width = desc.width;
		this.height = desc.height;
		this.image = manager.getImage(index, frame);
	}
	
	public Sprite(DataInputStream stream) {
		
	}
	
	public void writeTo(DataOutputStream stream) {
		
	}
	
	public void drawTo(Graphics g2d) {
		// Draw transformations here
		g2d.drawImage(image, x-centerX, y-centerY, null);
	}
	
}
