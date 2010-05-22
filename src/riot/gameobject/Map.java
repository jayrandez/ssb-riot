package riot.gameobject;

import riot.GameObject;
import riot.SpriteManager;
import riot.physics.AnimationPhysics;
import riot.physics.MapPhysics;

public class Map extends GameObject {

	public Map(SpriteManager manager, String mapName) {
		super(new MapPhysics(manager, mapName));
		AnimationPhysics physics = (AnimationPhysics)getPhysics();
		physics.setAnimation("maps", mapName, false, 0);
	}
}
