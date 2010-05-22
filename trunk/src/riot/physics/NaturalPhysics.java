package riot.physics;

import java.util.ArrayList;

import riot.*;

// Remember Velocity is measured in Pixels / Step or approx. Pixels / 30ms
// And positive y means downwards

public class NaturalPhysics extends AnimationPhysics {
	Size size;
	double gravity;
	double xVelocity;
	double yVelocity;
	boolean ignoreGravity;

	public NaturalPhysics(SpriteManager manager, Location location, Size size, double gravity) {
		super(manager, location);
		this.size = size;
		this.gravity = gravity;
		this.xVelocity = 0;
		this.yVelocity = 0;
		this.steps = 0;
		this.ignoreGravity = false;
	}
	
	public void ignoreGravity() {
		ignoreGravity = true;
	}
	
	public void step() {
		super.step();
		Location location = getLocation();
		if(!ignoreGravity)
			yVelocity += gravity;
		ignoreGravity = false;
		location.x += (xVelocity / 10.0);
		location.y += (yVelocity / 10.0);
		steps++;
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
		this.steps = 0;
		double radians = Math.PI * direction / 180.0;
		xVelocity = speed * Math.cos(radians);
		yVelocity = speed * -Math.sin(radians);
	}
	
	public void setInfluence(double speed, int direction) {
		
	}
	
	public void clearInfluence() {
		
	}
}
