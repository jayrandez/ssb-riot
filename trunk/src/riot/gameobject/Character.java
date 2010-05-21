package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	NaturalPhysics physics;
	
	public Character(SpriteManager manager, String sheetName) {
		super(new NaturalPhysics(manager));
		this.sheetName = sheetName;
		this.physics = (NaturalPhysics)getPhysics();
		idle();
	}

	public void idle() {
		physics.setAnimation(sheetName, "idle");
		physics.setMovement(0, 0);
	}
	
	public void move(int degrees) {
		if(degrees == -1 || degrees == 90 || degrees == 270)
			idle();
		else if(degrees < 90 || degrees > 270) {
			physics.setAnimation(sheetName, "shortWalk");
			physics.setMovement(0, 600);
		}
		else {
			physics.setAnimation(sheetName, "shortWalk" /*, REVERSED*/);
			physics.setMovement(180, 600);
		}
	}

	public void attack() {
		// TODO Auto-generated method stub
		
	}

	public void dodge() {
		// TODO Auto-generated method stub
	}

	public void jump() {
		physics.makeImpulse(90, 1000);
	}

	public void special() {
		// TODO Auto-generated method stub
	}

	public void shield() {
		// TODO Auto-generated method stub
	}
	
	public void death(int speed, int direction)//speed of flying away and 
	{											//direction of flying
		// TODO Auto-generated method stub
	}
}
