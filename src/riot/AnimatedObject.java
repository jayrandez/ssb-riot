package riot;

public abstract class AnimatedObject extends GameObject {
	SpriteManager manager;
	AnimationDescriptor descriptor;
	int index;
	boolean flipped;
	int rotation;
	int frame;
	int steps;
	
	public AnimatedObject(GameEngine engine, SpriteManager manager, Point location) {
		super(engine, location);
		this.manager = manager;
	}
	
	public void setAnimation(String sheetName, String animationName, int rotation) {
		this.rotation = rotation;
		this.index = manager.getIndex(sheetName, animationName);
		descriptor = manager.getAnimation(index);
		frame = 0;
		steps = 0;
	}
	
	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}
	
	public void clearAnimation() {
		descriptor = null;
		frame = 0;
		steps = 0;
	}
	
	public void step() {
		if(descriptor != null) {
			if(steps > descriptor.speed) {
				steps = 0;
				frame++;
				if(frame == descriptor.frames) {
					if(descriptor.repeat)
						frame = 0;
					else
						frame--;
				}
			}
		}
		steps++;
	}
	
	public Sprite getSprite() {
		return new Sprite(manager, index, frame, (int)getLocation().x, (int)getLocation().y, rotation, flipped);
	}
}
