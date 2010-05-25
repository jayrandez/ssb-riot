package riot;

import java.util.ArrayList;

public class FollowerObject extends GameObject {
	GameObject target;
	Size size;
	Size offset;
	
	public FollowerObject(GameEngine engine, SpriteManager manager, GameObject target) {
		super(engine, manager, new Point(0,0));
		this.target = target;
		this.size = new Size(0, 0);
		this.offset = new Size(0,0);
	}
	
	public FollowerObject(GameEngine engine, SpriteManager manager, GameObject target, Size size) {
		super(engine, manager, new Point(0,0));
		this.target = target;
		this.size = size;
		this.offset = new Size(0,0);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		Point loc = getLocation();
		ArrayList<Rectangle> send = new ArrayList<Rectangle>();
		send.add(new Rectangle(loc.x, loc.y, size.width, size.height));
		return send;
	}

	public void step() {
		Point targetLoc = target.getLocation();
		targetLoc.x += offset.width;
		targetLoc.y += offset.height;
		this.setLocation(targetLoc);
	}
	
	public void setOffset(Size offset) {
		this.offset = offset;
	}
	
	public GameObject getTarget() {
		return target;
	}
}
