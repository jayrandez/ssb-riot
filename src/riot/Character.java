package riot;

public class Character extends NaturalObject {

	String sheetName;
	int degrees;
	boolean aerial;
	boolean neutral;
	boolean direction;
	int maxJumps;
	int currentJumps;
	boolean justSpawned;
	SpawnPlatform platform;
	int damageTaken;
	boolean knockedUp;
	
	public Character(GameEngine engine, SpriteManager manager, String sheetName, Size size, int maxJumps, SpawnPlatform platform) {
		super(engine, manager, new Point(323, 97), size, 8.0);
		this.platform = platform;
		this.sheetName = sheetName;
		this.aerial = true;
		this.direction = Riot.Right;
		this.maxJumps = maxJumps;
		this.justSpawned = true;
		setAnimation(sheetName, "idle");
		move(-1);
		damageTaken = 0;
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
		if(justSpawned) {
			platform.dropCharacter();
		}
		setMovement();
	}

	// Result of Pressing F
	public void attack() {
		/* Create a charging attack */
		if(aerial == false) {
			Damager damager;
			if(degrees != -1)
				damager = new Damager(getEngine(), getManager(), this, new Size(50,50), degrees, 30);
			else if(direction == Riot.Right)
				damager = new Damager(getEngine(), getManager(), this, new Size(50,50), 0, 30);
			else
				damager = new Damager(getEngine(), getManager(), this, new Size(50,50), 180, 30);
			getEngine().spawnWorldObject(damager);
			damager.setLifetime(20);
			damager.step();
			
			setAnimation(this.sheetName, "punch");
		}
	}
	
	// Result of Pressing D
	public void special() {}

	// Result of Pressing S
	public void dodge() {}

	// Result of Pressing A
	public void shield() {}
	
	// Result of Pressing Space
	public void jump() {
		if(currentJumps < maxJumps) {
			setAnimation(sheetName, "jump");
			setMovement(160, 90);
			currentJumps++;
		}
	}
	
	// Result of Taking Damage
	public void damage(Damager damager) 
	{
		//accumulates amount of damage
		damageTaken += damager.getDamage();
		//sets the speed equal to the damage taken (100 damage = speed of walking)
		double speed = damageTaken * .4;
		int angle = 0;
		
		//determines which way character faces and adjusts
		//angle according to that direction
		if (direction == false)
		{
			angle = 180 - damager.getDirection();
			angle -= damageTaken / 50;
			
			setMovement(speed, angle);
			knockedUp = true;
		}
		else
		{
			angle = damager.getDirection() - 180;
			angle += damageTaken / 50;
			
			setMovement(speed, angle);
			knockedUp = true;
		}
	}
	// Result of Going Out of Bounds
	public void death(int speed, int direction) {
		stopMovement();
		damageTaken = 0;
	}
	
	// Step Function Indicating State (Grounded/Aerial)
	public void aerial(boolean aerial) {
		super.aerial(aerial);
		boolean before = this.aerial;
		this.aerial = aerial;
		
		// We just landed on top of a platform
		if(before == true && aerial == false) {
			stopMovement();
			stopInfluence();
			currentJumps = 0;
		}
		
		// The state has changed
		if(before != aerial) {
			setMovement();
		}
	}
	
	// Change the movement of character after controller has changed
	// or we have switched between being aerial or on a platform
	private void setMovement() {
		// We are in the air
		
		if(aerial) {
			// We didn't jump to get in the air, we just walked off or we hit by damage
			if(currentJumps == 0 && knockedUp == false) {
				stopMovement();
				currentJumps++;
			}
			if(!neutral) {
				// If we are in the air and holding a direction key
				if(direction == Riot.Right)
					setInfluence(40, 0);
				else
					setInfluence(40, 180);
			}
		}
		// We are on a platform
		else {
			setFlipped(direction);
			if(neutral == true) {
				stopMovement();
					setAnimation(sheetName, "idle");
			}
			else if(direction == Riot.Right){
				setMovement(60, 0);
					setAnimation(sheetName, "shortWalk");
			}
			else {
				setMovement(60, 180);
					setAnimation(sheetName, "shortWalk");
			}
		}
		knockedUp = false;
	}
}