package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Character extends GameObject {

	public Character(SpriteManager manager) {
		super(new CharacterPhysics(manager));
		CharacterPhysics physics = (CharacterPhysics)getPhysics();
		physics.setAnimation("jigglypuff", "walk");
	}
}
