package riot;

public class Character extends NaturalObject {

	String sheetName;
	int degrees;
	boolean aerial;
	boolean neutral;
	boolean direction;
	
	boolean startedInfluencingJump;
	
	public Character(SpriteManager manager, String sheetName, Size size) {
		super(manager, new Point(320, 450), size, 12.0);
		this.sheetName = sheetName;
		this.aerial = true;
		this.direction = Riot.Right;
		setAnimation(sheetName, "idle", false, 0);
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
		setMovement(160, 90);
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
		if(before == true && aerial == false) {
			stopMovement();
			stopInfluence();
			startedInfluencingJump = false;
			System.out.println("Collision");
		}
		if(before != aerial) {
			setMovement();
		}
	}
	
	private void setMovement() {
		if(!aerial) {
			if(neutral == true) {
				stopMovement();
				setAnimation(sheetName, "idle", direction, 0);
			}
			else if(direction == Riot.Right){
				setMovement(60, 0);
				setAnimation(sheetName, "shortWalk", direction, 0);
			}
			else {
				setMovement(60, 180);
				setAnimation(sheetName, "shortWalk", direction, 0);
			}
		}
		else {
			setAnimation(sheetName, "idle", direction, 0);
			if(neutral == true && !startedInfluencingJump) {
				stopInfluence();
			}
			else if(direction == Riot.Right) {
				setInfluence(60, 0);
				startedInfluencingJump = true;
			}
			else {
				setInfluence(60, 180);
				startedInfluencingJump = true;
			}
		}
	}
}
