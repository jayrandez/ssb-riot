package riot;

import java.util.*;

/**
 * An object which is stepped as part of the game loop which in practice can
 * be located in the game world or as an overlay on the SceneWindow's hud
 */
public abstract class GameObject {
	private Point location;
	private GameEngine engine;
	
	public GameObject(GameEngine engine, Point location) {
		this.engine = engine;
		this.location = location;
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
	 * Returns a reference to the game engine.
	 */
	public GameEngine getEngine() {
		return engine;
	}
	
	/**
	 * Returns the bounding box(es) representative of this GameObject
	 */
	public abstract ArrayList<Rectangle> getBoundingBoxes();
	
	/**
	 * Called at a frame rate of 30hz to be used at the objects discretion
	 * This contains the main logic of the object and update's it's state over time.
	 */
	public abstract void step();
}
