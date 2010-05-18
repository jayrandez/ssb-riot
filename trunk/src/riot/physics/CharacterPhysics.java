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
	double gravity;
	int time;
	
	double x;
	double y;
	
	public CharacterPhysics(SpriteManager manager) {
		this.manager = manager;
		animation = null;
		animationSteps = 0;
		x = (int)(Math.random() * 200);
		y = 300;
		time = 0;
		speedX = 1;
		speedY = 0;
		gravity = .2;
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
		x += speedX;
		//y += speedY + (time * gravity);
		animationSteps++;
		time++;
	}
	
	public void setAnimation(String sheet, String animation) {
		animationSteps = 0;
		frame = 0;
		this.sheet = sheet;
		this.animation = animation;
		this.descriptor = manager.getAnimation(sheet, animation);
	}
}
