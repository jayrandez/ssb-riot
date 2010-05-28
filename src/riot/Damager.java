package riot;

public class Damager extends FollowerObject
{
	private int damage;
	private boolean wasUsed;
	private int lifetime;
	private int steps;
	private int degrees;
	private Character character;
	//creates a DamageObject with a offset (direction), a range (hitLength), a damage amount (damageAmount), and a size (dmgSize)
	//default location created at center of the character (offset of 315)
	public Damager(GameEngine engine, SpriteManager manager, GameObject character, Size size, int degrees, int damage)
	{
		super(engine, manager, character, size);
		this.damage = damage;
		this.character = (Character) character;
		chooseOffset(degrees);
		this.steps = 0;
		this.lifetime = -1;
		this.degrees = degrees;
	}
	
	//rotates the origin point of the DamageObject based on the degrees offset it is given
	public void chooseOffset(double degrees)
	{
		GameObject target = getTarget();
		double offsetY = -target.getSize().height/2;
		double offsetX = 0;
		
		if(degrees == 0)
		{
			offsetY -= getSize().height/2;
		}
		else if(degrees == 45)
		{
			offsetY -= getSize().height;
		}
		else if(degrees == 90)
		{
			offsetX -= getSize().width/2;
			offsetY -= getSize().height;
		}
		else if(degrees == 135)
		{
			offsetX -= getSize().width;
			offsetY -= getSize().height;
		}
		else if(degrees == 180)
		{
			offsetX -= getSize().width;
			offsetY -= getSize().height/2;
		}
		else if(degrees == 225)
		{
			offsetX -= getSize().width;
		}
		else if(degrees == 270)
		{
			offsetX -= getSize().width/2;
		}
		
		setOffset(new Size(offsetX, offsetY));
	}
	
	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public void wasUsed() {
		wasUsed = true;
	}
	
	public void step() {
		super.step();
		if (wasUsed == true)
			damage = 0;
		if(steps == lifetime) {
			if (character.degrees == -1)
				character.setAnimation(character.sheetName, "idle");
			else
				character.setAnimation(character.sheetName, "shortWalk");
			getEngine().removeWorldObject(this);
			character.damager = null;
		}
		steps++;
	}
	
	public int getDirection() {
		return degrees;
	}
}
