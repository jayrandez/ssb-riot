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
		rotateOffset();
	}
	
	//rotates the origin point of the DamageObject based on the degrees offset it is given
	public void rotateOffset()
	{
		GameObject target = getTarget();
		double offsetY = -target.getBoundingBoxes().get(0).height/2;
		double offsetX = 0;
		
		if(direction == 0)
		{
			offsetX = 0;
			offsetY += offsetY;
		}
		else if(direction == 45)
		{
			offsetX = 0;
			offsetY += offsetY * 2;
		}
		else if(direction == 90)
		{
			offsetX = (int) - target.getBoundingBoxes().get(0).width/2;
			offsetY += offsetY * 2;
		}
		else if(direction == 135)
		{
			offsetX = (int) - target.getBoundingBoxes().get(0).width;
			offsetY += offsetY * 2;
		}
		else if(direction == 180)
		{
			offsetX = (int) - target.getBoundingBoxes().get(0).width;
			offsetY += offsetY;
		}
		else if(direction == 225)
		{
			offsetX = (int) - target.getBoundingBoxes().get(0).width;
		}
		else if(direction == 270)
		{
			offsetX = (int) - target.getBoundingBoxes().get(0).width/2;
		}
		
		super.setOffset(new Size(offsetX, offsetY));
	}

	//causes the character that comes in contact with the DamageObject to take damage
	public int causeDamage()
	{
		return damageAmount;
	}

}
