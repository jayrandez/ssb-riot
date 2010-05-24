package riot;

import java.util.ArrayList;

public class DamageObject extends FollowerObject
{
	private int damageAmount;
	private int hitLength;
	private int direction;
	
	public DamageObject(GameEngine engine, SpriteManager m, GameObject character, Size s, int dir, int hit, int dmg)
	{
		super(engine, m, character, s);
		direction = dir;
		hitLength = hit;
		damageAmount = dmg;
	}

	//causes the character that comes in contact with the DamageObject to take damage
	public int causeDamage()
	{
		return damageAmount;
	}

}
