package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	NaturalPhysics physics;
	int degrees;
	boolean grounded;
	int direction;
	
	public Character(SpriteManager manager, String sheetName, Size size) {
		super(new NaturalPhysics(manager, new Location(320, 450), size, 5.0));
		this.sheetName = sheetName;
		this.physics = (NaturalPhysics)getPhysics();
		this.grounded = false;
		this.direction = Riot.Right;
		physics.setAnimation(sheetName, "idle", false, 0);
	}
	
	// Result of Altering Arrow Keys
	public void move(int degrees) {
		this.degrees = degrees;
		if(degrees < 90 || degrees > 270)
			this.direction = Riot.Right;
		else
			this.direction = Riot.Left;
	}

	// Result of Pressing F
	public void attack() {}
	
	// Result of Pressing D
	public void special() {}

	// Result of Pressing S
	public void dodge() {}

	// Result of Pressing A
	public void shield() {}
	
	// Result of Pressing Space
	public void jump() {
		physics.setMovement(120, 90);
	}
	
	// Result of Taking Damage
	public void damage(int damage) {}

	// Result of Going Out of Bounds
	public void death(int speed, int direction) {}
	
	// Result of Colliding W/ Platform
	public void mapCollision() {
		physics.clearInfluence();
	}
	
	// Result of Standing on a Platform (Step)
	public void grounded() {
		grounded = true;
	}
	
	// Result of Being Airborn (Step)
	public void aerial() {
		grounded = false;
	}
}
