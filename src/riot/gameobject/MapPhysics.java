package riot.gameobject;

import riot.Physics;
import riot.Sprite;
import riot.SpriteManager;

public class MapPhysics extends Physics {
	SpriteManager manager;
	String mapName;
	
	public MapPhysics(SpriteManager manager, String mapName) {
		this.manager = manager;
		this.mapName = mapName;
	}
	public Sprite getSprite() {
		return new Sprite(manager, "maps", mapName, 0, 0, 0, 0);
	}

	public int getX() {
		return 0;
	}

	public int getY() {
		return 0;
	}

	public void step() {}
}
