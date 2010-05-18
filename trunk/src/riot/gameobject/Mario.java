package riot.gameobject;

import riot.*;
import riot.physics.*;

public class Mario extends GameObject{
	
	public Mario(SpriteManager manager) {
		super(new CharacterPhysics(manager));
		CharacterPhysics physics = (CharacterPhysics)getPhysics();
		physics.setAnimation("mario", "idle");
	}

}
