package riot;

import java.util.*;

public abstract class GameObject {
	private Point location;
	
	public GameObject(Point location) {
		this.location = location;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public abstract ArrayList<Rectangle> getBoundingBoxes();
	public abstract void step();
}
