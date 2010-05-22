package riot;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

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
	
	public void writeTo(DataOutputStream stream) throws IOException {
		stream.writeShort(index);
		stream.writeShort(frame);
		stream.writeShort(x);
		stream.writeShort(y);
		stream.writeShort(rotation);
		stream.writeBoolean(flipped);
	}
	
	public void drawTo(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform transform = new AffineTransform();
		transform.translate(x-centerX, y-centerY);
		if(flipped) {
			transform.translate(width, 0);
			transform.scale(-1, 1);
		}
		g2d.drawImage(image, transform, null);
	}
	
}
