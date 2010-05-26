package riot;

import java.util.ArrayList;

public class FollowerObject extends GameObject {
	GameObject target;
	Size size;
	Size offset;
	
	public FollowerObject(GameEngine engine, SpriteManager manager, GameObject target, Size size) {
		super(engine, manager, new Point(0,0), size);
		this.target = target;
		this.size = size;
		this.offset = new Size(0,0);
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
