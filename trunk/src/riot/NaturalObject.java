package riot;

import java.util.*;

public class NaturalObject extends AnimatedObject {
	Size size;
	double gravity;
	double xVelocity;
	double yVelocity;
	double xInfluence;
	double yInfluence;
	boolean ignoreGravity;

	public NaturalObject(GameEngine engine, SpriteManager manager, Point location, Size size, double gravity) {
		super(engine, manager, location);
		this.size = size;
		this.gravity = gravity;
		this.xVelocity = 0;
		this.yVelocity = 0;
		this.steps = 0;
		this.ignoreGravity = false;
	}
	
	public void aerial(boolean aerial) {
		ignoreGravity = !aerial;
	}
	
	public void step() {
		super.step();
		Point location = getLocation();
		if(!ignoreGravity)
			yVelocity += gravity;
		ignoreGravity = false;
		location.x += ((xVelocity + xInfluence)/ 10.0);
		location.y += ((yVelocity + yInfluence)/ 10.0);
		setLocation(location);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		double width = size.width;
		double height = size.height;
		double left = getLocation().x - (width / 2);
		double top = getLocation().y - height + 1;
		Rectangle bounds = new Rectangle(left, top, width, height);
		ArrayList<Rectangle> send = new ArrayList<Rectangle>();
		send.add(bounds);
		return send;
	}
	
	public void setMovement(double speed, int direction) {
		double radians = Math.PI * direction / 180.0;
		xVelocity = speed * Math.cos(radians);
		yVelocity = speed * -Math.sin(radians);
	}
	
	public void stopMovement() {
		xVelocity = 0.0;
		yVelocity = 0.0;
	}
	
	public void setInfluence(double speed, int direction) {
		double radians = Math.PI * direction / 180.0;
		xInfluence = speed * Math.cos(radians);
		yInfluence = speed * -Math.sin(radians);
	}
	
	public void stopInfluence() {
		xInfluence = 0.0;
		yInfluence = 0.0;
	}
}
