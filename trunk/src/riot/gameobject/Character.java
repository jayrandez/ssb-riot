package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	String sheetName;
	NaturalPhysics physics;
	int degrees;
	boolean aerial;
	boolean neutral;
	boolean direction;
	
	boolean startedInfluencingJump;
	
	public Character(SpriteManager manager, String sheetName, Size size) {
		super(new NaturalPhysics(manager, new Location(320, 450), size, 12.0));
		this.sheetName = sheetName;
		this.physics = (NaturalPhysics)getPhysics();
		this.aerial = true;
		this.direction = Riot.Right;
		physics.setAnimation(sheetName, "idle", false, 0);
		move(-1);
	}
	
	// Result of Altering Arrow Keys
	public void move(int degrees) {
		this.degrees = degrees;
		if(degrees == 90 || degrees == 270 || degrees == -1) {
			neutral = true;
		}
		else if(degrees < 90 || degrees > 270) {
			this.direction = Riot.Right;
			neutral = false;
		}
		else if(degrees > 90 && degrees < 270) {
			this.direction = Riot.Left;
			neutral = false;
		}
		
		setMovement();
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
		physics.setMovement(160, 90);
	}
	
	// Result of Taking Damage
	public void damage(int damage) {}

	// Result of Going Out of Bounds
	public void death(int speed, int direction) {}
	
	// Result of Colliding W/ Platform
	public void mapCollision() {
		physics.stopMovement();
		physics.stopInfluence();
		startedInfluencingJump = false;
		System.out.println("Collision");
	}
	
	// Step Function Indicating State (Grounded/Aerial)
	public void aerial(boolean aerial) {
		boolean before = this.aerial;
		this.aerial = aerial;
		if(before == true && aerial == false) {
			mapCollision();
		}
		if(before != aerial) {
			setMovement();
		}
	}
	
	private void setMovement() {
		if(!aerial) {
			if(neutral == true) {
				physics.stopMovement();
				physics.setAnimation(sheetName, "idle", direction, 0);
			}
			else if(direction == Riot.Right){
				physics.setMovement(60, 0);
				physics.setAnimation(sheetName, "shortWalk", direction, 0);
			}
			else {
				physics.setMovement(60, 180);
				physics.setAnimation(sheetName, "shortWalk", direction, 0);
			}
		}
		else {
			physics.setAnimation(sheetName, "idle", direction, 0);
			if(neutral == true && !startedInfluencingJump) {
				physics.stopInfluence();
			}
			else if(direction == Riot.Right) {
				physics.setInfluence(60, 0);
				startedInfluencingJump = true;
			}
			else {
				physics.setInfluence(60, 180);
				startedInfluencingJump = true;
			}
		}
	}
}
