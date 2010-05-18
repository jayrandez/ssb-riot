package riot.physics;

import riot.Physics;
import riot.Sprite;
import riot.SpriteManager;
import java.util.*;
import java.awt.*;

public class MapPhysics extends Physics {
	SpriteManager manager;
	String mapName;
	ArrayList<Rectangle> solidAreas;
	
	public MapPhysics(SpriteManager manager, String mapName) {
		this.manager = manager;
		this.mapName = mapName;
		solidAreas = new ArrayList<Rectangle>();
		solidAreas.add(new Rectangle(206, 680, 513, 49));
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
