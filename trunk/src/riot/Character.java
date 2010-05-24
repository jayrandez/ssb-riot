package riot;

public class Character extends NaturalObject {

	String sheetName;
	int degrees;
	boolean aerial;
	boolean neutral;
	boolean direction;
	int maxJumps;
	int currentJumps;
	
	public Character(GameEngine engine, SpriteManager manager, String sheetName, Size size, int maxJumps) {
		super(engine, manager, new Point(323, 97), size, 12.0);
		this.sheetName = sheetName;
		this.aerial = true;
		this.direction = Riot.Right;
		this.maxJumps = maxJumps;
		setAnimation(sheetName, "idle", 0);
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
	public void attack() {
		FollowerObject follower = new FollowerObject(getEngine(), this, new Size(100,50), new Size(0,0));
		getEngine().spawnWorldObject(follower);
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
			setAnimation(sheetName, "jump", 0);
			setMovement(160, 90);
			currentJumps++;
		}
	}
	
	// Result of Taking Damage
	public void damage(int damage) {}

	// Result of Going Out of Bounds
	public void death(int speed, int direction) {}
	
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
			// We didn't jump to get in the air, we just walked off.
			if(currentJumps == 0)
				stopMovement();
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
				setAnimation(sheetName, "idle", 0);
			}
			else if(direction == Riot.Right){
				setMovement(60, 0);
				setAnimation(sheetName, "shortWalk", 0);
			}
			else {
				setMovement(60, 180);
				setAnimation(sheetName, "shortWalk", 0);
			}
		}
	}
}
