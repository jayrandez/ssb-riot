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
		x = 250;
		y = 250;
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
				steps = 0;
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
		this.sheet = sheet;
		this.animation = animation;
		this.descriptor = manager.getAnimation(sheet, animation);
	}
}
