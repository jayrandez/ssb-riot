package riot;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
	public int width;
	public int height;
	
	public Image image;
	
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
		this.width = desc.width;
		this.height = desc.height;
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
		this.width = desc.width;
		this.height = desc.height;

		this.image = manager.getImage(index, frame);
		
	}
	
	public void drawOn(Graphics g2d) {
		g2d.drawImage(image, x-centerX, y-centerY, null);
	}

	private BufferedImage horizontalMirror(BufferedImage p)
	{
		//for even width pixels
		if (p.getWidth() % 2 == 0)
		{

			for (int i = 0, k = p.getWidth() - 1; i < k; i++, k--)
			{
				for (int j = 0, l = 0; j < p.getHeight(); j++, l++)
				{
					int x = p.getRGB(i, j);
					int y = p.getRGB(k, l);
					p.setRGB(i, j, y);
					p.setRGB(k, l, x);
				}
			}
		}
		//for odd width pixels
		else
		{
			for (int i = 0, k = p.getWidth() - 1; i != k; i++, k--)
			{
				for (int j = 0, l = 0; j < p.getHeight(); j++, l++)
				{
					int x = p.getRGB(i, j);
					int y = p.getRGB(k, l);
					p.setRGB(i, j, y);
					p.setRGB(k, l, x);
				}
			}
		}

		return p;
	}
}
