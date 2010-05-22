package riot.physics;

import riot.AnimationDescriptor;
import riot.Location;
import riot.Physics;
import riot.Sprite;
import riot.SpriteManager;

public abstract class AnimationPhysics extends Physics {
	SpriteManager manager;
	AnimationDescriptor descriptor;
	int index;
	boolean flipped;
	int rotation;
	int frame;
	int steps;
	
	public AnimationPhysics(SpriteManager manager, Location location) {
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
