package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	NaturalPhysics physics;
	
	public Character(SpriteManager manager, String sheetName, Size size) {
		super(new NaturalPhysics(manager, new Location(320, 450), size, 5.0));
		this.sheetName = sheetName;
		this.physics = (NaturalPhysics)getPhysics();
		idle();
	}

	public void idle() {
		physics.setAnimation(sheetName, "idle", false, 0);
		physics.setMovement(0,0);
	}
	
	public void move(int degrees) {
		if(degrees == -1 || degrees == 90 || degrees == 270) {
			idle();
		}
		else if(degrees < 90 || degrees > 270) {
			physics.setAnimation(sheetName, "shortWalk", false, 0);
			physics.setMovement(60,0);
		}
		else {
			physics.setAnimation(sheetName, "shortWalk", true, 0);
			physics.setMovement(60, 180);
		}
	}

	public void attack() {
	}

	public void dodge() {
	}

	public void jump() {
		physics.setMovement(120, 90);
	}

	public void special() {
	}

	public void shield() {
	}
	
	public void death(int speed, int direction) {
	}
	
	public void mapCollision() {
		physics.setMovement(0, 0);
	}
}
