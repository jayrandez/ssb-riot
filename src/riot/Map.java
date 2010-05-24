package riot;

import java.util.*;

public class Map extends AnimatedObject {
	ArrayList<Rectangle> platforms;

	public Map(GameEngine engine, SpriteManager spriteManager, MapManager mapManager, String mapName) {
		super(engine, spriteManager, new Point(0,0));
		setAnimation("maps", mapName, 0);
		platforms = mapManager.platformsIn(mapName);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		return platforms;
	}
}
