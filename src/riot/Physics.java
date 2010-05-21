package riot;

import java.util.ArrayList;

public abstract class Physics {
	private Location location;
	
	public Physics(Location location) {
		this.location = location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public abstract ArrayList<Rectangle> getBoundingBoxes();
	public abstract void step();
}
