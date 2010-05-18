package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Jigglypuff extends GameObject {

	public Jigglypuff(SpriteManager manager) {
		super(new CharacterPhysics(manager));
		CharacterPhysics physics = (CharacterPhysics)getPhysics();
		physics.setAnimation("jigglypuff", "idle");
	}
}
