package riot;

import java.util.*;

/**
 * An object which is stepped as part of the game loop which in practice can
 * be located in the game world or as an overlay on the SceneWindow's hud
 */
public abstract class GameObject {
	private Point location;
	private Size size;
	private GameEngine engine;
	private SpriteManager manager;
	private AnimationDescriptor descriptor;
	private int index;
	private boolean flipped;
	private int rotation;
	private int frame;
	private int steps;
	
	public GameObject(GameEngine engine, SpriteManager manager, Point location, Size size) {
		this.manager = manager;
		this.engine = engine;
		this.location = location;
		this.size = size;
	}

	public void setAnimation(String sheetName, String animationName) {
		this.index = manager.getIndex(sheetName, animationName);
		descriptor = manager.getAnimation(index);
		frame = 0;
		steps = 0;
	}
	
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	public void clearAnimation() {
		descriptor = null;
		frame = 0;
		steps = 0;
	}
	
	public void step() {
		if(descriptor != null) {
			if(steps > descriptor.speed) {
				steps = 0;
				frame++;
				if(frame == descriptor.frames) {
					if(descriptor.repeat)
						frame = 0;
					else
						frame--;
				}
			}
		}
		steps++;
	}
	
	public Sprite getSprite() {
		if(descriptor == null)
			return null;
		return new Sprite(manager, index, frame, (int)getLocation().x, (int)getLocation().y, rotation, flipped);
	}
	
	/**
	 * Update the location of this GameObject
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	
	/**
	 * Returns a copy of this objects location
	 */
	public Point getLocation() {
		return (Point)(location.clone());
	}
	
	/**
	 * Returns a reference to the game engine
	 */
	public GameEngine getEngine() {
		return engine;
	}
	
	/**
	 * Returns a reference to the sprite manager
	 */
	public SpriteManager getManager() {
		return manager;
	}
	
	/**
	 * Returns a copy of the objects size
	 */
	public Size getSize() {
		return (Size)size.clone();
	}
	
	/**
	 * Returns the bounding box(es) representative of this GameObject
	 */
	public abstract ArrayList<Rectangle> getBoundingBoxes();
}
