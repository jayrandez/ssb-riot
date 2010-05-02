package riot;

import java.awt.Graphics2D;

public class GameObject {
	private Physics objectPhysics;
	
	public GameObject(Physics objectPhysics) {
		this.objectPhysics = objectPhysics;
	}
	
	public void getPhysics() {
		return objectPhysics();
	}
	
	public void step() {
		objectPhysics.step();
	}
	public void draw(Graphics2D g2d);
}
