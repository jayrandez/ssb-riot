package riot;

import java.util.ArrayList;

public class FollowerObject extends GameObject {
	GameObject target;
	Size size;
	Size offset;
	
	public FollowerObject(GameEngine engine, GameObject target, Size size, Size offset) {
		super(engine, new Point(0,0));
		this.target = target;
		this.size = size;
		this.offset = offset;
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
}
