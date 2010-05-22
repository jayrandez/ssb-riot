package riot;

public abstract class AnimatedObject extends GameObject {
	SpriteManager manager;
	AnimationDescriptor descriptor;
	int index;
	boolean flipped;
	int rotation;
	int frame;
	int steps;
	
	public AnimatedObject(SpriteManager manager, Point location) {
		super(location);
		this.manager = manager;
	}
	
	public void setAnimation(String sheetName, String animationName, boolean flipped, int rotation) {
		this.flipped = flipped;
		this.rotation = rotation;
		this.index = manager.getIndex(sheetName, animationName);
		descriptor = manager.getAnimation(index);
		frame = 0;
		steps = 0;
	}
	
	public void clearAnimation() {
		descriptor = null;
		frame = 0;
		steps = 0;
	}
	
	public void step() {
		if(descriptor != null) {
			if(steps > descriptor.speed) {
				frame++;
				steps = 0;
				if(frame == descriptor.frames) {
					frame = 0;
				}
			}
		}
		steps++;
	}
	
	public Sprite getSprite() {
		return new Sprite(manager, index, frame, (int)getLocation().x, (int)getLocation().y, rotation, flipped);
	}
}
