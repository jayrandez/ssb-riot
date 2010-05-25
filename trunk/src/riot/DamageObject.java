package riot;

import java.util.ArrayList;

public class DamageObject extends FollowerObject
{
	private int damageAmount;
	private int hitLength;
	private int direction;
	private Size dmgSize;
	
	//creates a DamageObject with a offset (direction), a range (hitLength), a damage amount (damageAmount), and a size (dmgSize)
	//default location created at center of the character (offset of 315)
	public DamageObject(GameEngine engine, SpriteManager m, GameObject character, Size s, int dir, int hit, int dmg)
	{
		super(engine, m, character, s);
		direction = dir;
		hitLength = hit;
		damageAmount = dmg;
		dmgSize = s;
		super.setLocation(new Point (character.getLocation().x, character.getLocation().y - character.getBoundingBoxes().get(0).y/2));
	}
	
	//rotates the origin point of the DamageObject based on the degrees offset it is given
	public void setOffset()
	{
		if(direction == 0)
		{
			super.setLocation(new Point(this.getLocation().x , this.getLocation().y - dmgSize.height /2 ));
		}
		else if(direction == 45)
		{
			super.setLocation(new Point(this.getLocation().x, this.getLocation().y - dmgSize.height));
		}
		else if(direction == 90)
		{
			super.setLocation(new Point(this.getLocation().x - dmgSize.width / 2, this.getLocation().y - dmgSize.height));
		}
		else if(direction == 135)
		{
			super.setLocation(new Point(this.getLocation().x - dmgSize.width, this.getLocation().y - dmgSize.height));
		}
		else if(direction == 180)
		{
			super.setLocation(new Point(this.getLocation().x - dmgSize.width, this.getLocation().y - dmgSize.height / 2));
		}
		else if(direction == 225)
		{
			super.setLocation(new Point(this.getLocation().x - dmgSize.width, this.getLocation().y));
		}
		else if(direction == 270)
		{
			super.setLocation(new Point(this.getLocation().x - dmgSize.width / 2, this.getLocation().y));
		}
		else if(direction == 315)
		{
			super.setLocation(new Point(this.getLocation().x, this.getLocation().y));
		}
	}

	//causes the character that comes in contact with the DamageObject to take damage
	public int causeDamage()
	{
		return damageAmount;
	}

}
