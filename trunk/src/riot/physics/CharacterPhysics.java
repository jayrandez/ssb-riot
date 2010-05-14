package riot.physics;

import riot.*;

public class CharacterPhysics extends Physics {
	
	SpriteManager manager;
	int steps;
	
	AnimationDescriptor descriptor;
	String sheet;
	String animation;
	int frame;
	
	int x;
	int y;
	
	public CharacterPhysics(SpriteManager manager) {
		this.manager = manager;
		animation = null;
		steps = 0;
	}

	public Sprite getSprite() {
		return new Sprite(manager, sheet, animation, frame, x, y, 0);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void step() {
		if(animation != null) {
			if(steps > descriptor.speed) {
				frame++;
				if(frame == descriptor.frames) {
					frame = 0;
				}
			}
		}
		steps++;
	}
	
	public void setAnimation(String sheet, String animation) {
		steps = 0;
		frame = 0;
		this.descriptor = manager.getAnimation(sheet, animation);
	}
}
