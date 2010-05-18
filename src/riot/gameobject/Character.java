package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	CharacterPhysics physics;
	
	public Character(SpriteManager manager, String sheetName) {
		super(new CharacterPhysics(manager));
		this.sheetName = sheetName;
		this.physics = (CharacterPhysics)getPhysics();
		physics.setAnimation(sheetName, "idle");
	}
	
	public void idle() {
		physics.setAnimation(sheetName, "idle");
		physics.setMovement(0, 0);
	}
	
	public void walk(int direction) {
		physics.setAnimation(sheetName, "walk");
	}
	
	public void attack() {
		physics.setAnimation(sheetName, "attack");
	}
	
	public void smash(int direction) {
		physics.setAnimation(sheetName, "smash");
	}
	
	public void jump() {
		physics.setAnimation(sheetName, "jump");
		physics.makeImpulse(90, 10);
	}
	
	/* ... ETC for whatever functions a generic character can perform */
}
