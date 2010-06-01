package riot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Character extends NaturalObject {

	// About Me
	String sheetName;
	Player player;
	Label damageMeter;
	SpawnPlatform platform;
	
	// Movement Information
	int degrees;
	boolean aerial;
	boolean neutral;
	boolean direction;
	
	// Action Limitations
	Timer stunTimer;
	int maxJumps;
	int currentJumps;
	boolean stunned;
	int damageTaken;
	
	
	public Character(GameEngine engine, SpriteManager manager, String sheetName, Size size, int maxJumps, SpawnPlatform platform, Player player) {
		super(engine, manager, new Point(323, 97), size, 8.0);
		this.platform = platform;
		this.sheetName = sheetName;
		this.aerial = true;
		this.direction = Riot.Right;
		this.maxJumps = maxJumps;
		this.player = player;
		setAnimation(sheetName, "idle");
		move(-1);
		damageTaken = 0;
		
		stunTimer = new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stunned = false;
				stunTimer.stop();
				damageMeter.setFont("DamageMeter");
				setMovement();
			}
		});
	}
	
	public void setDamageMeter(Label damageMeter) {
		this.damageMeter = damageMeter;
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
		platform.dropCharacter();
		setMovement();
	}

	// Result of Pressing F
	public void attack() {
		if(!stunned) {
			Damager damager = null;
			if(aerial == false) {
				int damage = (neutral) ? (15) : (35);
				if(direction == Riot.Right)
					damager = new Damager(getEngine(), getManager(), this, new Size(28,28), 0, damage);
				else if (direction == Riot.Left)
					damager = new Damager(getEngine(), getManager(), this, new Size(28,28), 180, damage);
				getEngine().spawnWorldObject(damager);
				damager.setLifetime(20);
				setAnimation(this.sheetName, "punch");
				stun(750);
			}
			else {
				// Aerial Attack (Ugh)
			}
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
			setMovement(140, 90);
			currentJumps++;
		}
	}
	
	// Result of Taking Damage
	public void damage(Damager damager) 
	{
		// If you want to change the dynamics of damage you should
		// probably  just look into changing these constants.
		final double speedMultiplier = .75;
		final double angleMultiplier = 13.0;
		final double stunTimeMultiplier = 40.0;
		final double initialAngle = 30;
		
		damageTaken += damager.getDamage();
		if(damageMeter != null)
			damageMeter.setText("" + damageTaken + "%");
		
		double speed = 100 + damageTaken * speedMultiplier;
		int angle = damager.getDirection();
		if(!aerial)
			if(angle > 0 && angle < 180)
				angle = makeUpwards(angle);
		if(angle == 180)
			angle -= initialAngle + damageTaken / angleMultiplier;
		else if(angle == 0)
			angle += initialAngle + damageTaken / angleMultiplier;
		setMovement(speed, angle);
		
		stun((int)(damager.getDamage() * stunTimeMultiplier));
		
		System.out.println("I'm hit! Angle: " + angle + " Speed: " + speed);
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
		if(!stunned) {
			if(aerial) {
				// We didn't jump to get in the air, we just walked off or we hit by damage
				if(currentJumps == 0) {
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
		}
	}
	
	void stun(int millis) {
		stunTimer.setInitialDelay(millis);
		stunTimer.setDelay(millis);
		stunTimer.start();
		stunned = true;
		damageMeter.setFont("DamageMeterRed");
	}
	
	int makeUpwards(int angle) {
		if(angle == 270)
			return 90;
		if(angle == 225)
			return 135;
		if(angle == 315)
			return 45;
		return 0;
	}
	

}
