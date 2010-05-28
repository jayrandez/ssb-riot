package riot;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

/**
 * The class representing all facets of an individual sprite of an animation
 */
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
	
	public Sprite() {
		image = null;
	}
	
	public Sprite(SpriteManager manager, int index, int frame, int x, int y, int rotation, boolean flipped) {
		this.index = index;
		this.frame = frame;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.flipped = flipped;
		
		AnimationDescriptor desc = manager.getAnimation(index);
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;
		this.width = desc.width;
		this.height = desc.height;
		this.image = manager.getImage(index, frame);
	}
	
	/**
	 * Creates the sprite from a network data stream
	 */
	public Sprite(SpriteManager manager, DataInputStream stream) throws IOException {
		this.index = stream.readShort();
		this.frame = stream.readShort();
		this.x = stream.readShort();
		this.y = stream.readShort();
		this.rotation = stream.readShort();
		this.flipped = stream.readBoolean();
		
		AnimationDescriptor desc = manager.getAnimation(index);
		this.centerX = desc.centerX;
		this.centerY = desc.centerY;
		this.width = desc.width;
		this.height = desc.height;
		this.image = manager.getImage(index, frame);
	}
	
	/**
	 * Serialize the sprite for transport over the network
	 */
	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeByte(Riot.StandardSprite);
		stream.writeShort(index);
		stream.writeShort(frame);
		stream.writeShort(x);
		stream.writeShort(y);
		stream.writeShort(rotation);
		stream.writeBoolean(flipped);
	}
	
	/**
	 * Draw the sprite on the given graphics context
	 */
	public void drawTo(Graphics g) {
		AffineTransform spriteTransform = new AffineTransform();
		if(flipped) {
			spriteTransform.scale(-1, 1);
			spriteTransform.translate(-width, 0);
			spriteTransform.translate(-(x-centerX), y-centerY);
		}
		else {
			spriteTransform.scale(1, 1);
			spriteTransform.translate(x-centerX, y-centerY);
		}
		
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(image, spriteTransform, null);
	}
	
}
