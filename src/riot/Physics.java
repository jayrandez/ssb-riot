package riot;

import java.awt.Dimension;
import java.awt.Rectangle;

public abstract class Physics {
	public abstract Dimension getLocation();
	public abstract Rectangle getBoundingBox();
	public abstract Sprite getSprite();
	public abstract void step();
}
