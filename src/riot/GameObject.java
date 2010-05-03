package riot;

import java.awt.Graphics2D;

public abstract class GameObject {
	private Physics objectPhysics;
	
	public GameObject(Physics objectPhysics) {
		this.objectPhysics = objectPhysics;
	}
	
	public void getPhysics() {
	}
	
	abstract public void step();
	abstract public void draw(Graphics2D g2d);
}
