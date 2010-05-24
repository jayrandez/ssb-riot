package riot;

import java.util.*;

public abstract class GameObject {
	private Point location;
	private GameEngine engine;
	
	public GameObject(GameEngine engine, Point location) {
		this.engine = engine;
		this.location = location;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public Point getLocation() {
		return (Point)(location.clone());
	}
	
	public GameEngine getEngine() {
		return engine;
	}
	
	public abstract ArrayList<Rectangle> getBoundingBoxes();
	public abstract void step();
}
