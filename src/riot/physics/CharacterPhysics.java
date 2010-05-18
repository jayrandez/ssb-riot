package riot.physics;

import riot.*;

public class CharacterPhysics extends Physics {
	
	SpriteManager manager;
	int animationSteps;
	AnimationDescriptor descriptor;
	String sheet;
	String animation;
	int frame;

	double speedX;
	double speedY;
	double x;
	double y;
	int time;
	
	double gravity;
	
	public CharacterPhysics(SpriteManager manager) {
		this.manager = manager;
		animation = null;
		animationSteps = 0;
		x = (int)(Math.random() * 200);
		y = 300;
		time = 0;
	}

	public Sprite getSprite() {
		return new Sprite(manager, sheet, animation, frame, (int)x, (int)y, 0);
	}

	public int getX() {
		return (int)x;
	}

	public int getY() {
		return (int)y;
	}

	public void step() {
		if(animation != null) {
			if(animationSteps > descriptor.speed) {
				frame++;
				animationSteps = 0;
				if(frame == descriptor.frames) {
					frame = 0;
				}
			}
		}
		animationSteps++;
		
		time++;
		// account for x, y, x speed, y speed, gravity, time passed (maybe wind resistance future)
		// update the x and y location
		
	}
	
	public void setAnimation(String sheet, String animation) {
		animationSteps = 0;
		frame = 0;
		this.sheet = sheet;
		this.animation = animation;
		this.descriptor = manager.getAnimation(sheet, animation);
	}
	
	void setDirection(int degrees, double speed) {
		// update x and y velocity based on degrees, speed
	}
}
