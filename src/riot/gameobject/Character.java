package riot.gameobject;

import java.awt.event.KeyEvent;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	CharacterPhysics physics;
	
	public Character(SpriteManager manager, String sheetName) {
		super(new CharacterPhysics(manager));
		this.sheetName = sheetName;
		this.physics = (CharacterPhysics)getPhysics();
		idle();
	}

	public void idle() {
		physics.setAnimation(sheetName, "idle");
	}
	
	public void move(int degrees) {
		if(degrees == -1)
			idle();
		else
			physics.setAnimation(sheetName, "shortWalk");
	}

	public void attack() {
		// TODO Auto-generated method stub
		
	}

	public void dodge() {
		// TODO Auto-generated method stub
		
	}

	public void jump() {
		// TODO Auto-generated method stub
		
	}

	public void special() {
		// TODO Auto-generated method stub
		
	}

	public void shield() {
		// TODO Auto-generated method stub
		
	}
}
