package riot;

import java.util.*;

public class Map extends AnimatedObject {
	ArrayList<Rectangle> platforms;

	public Map(SpriteManager spriteManager, MapManager mapManager, String mapName) {
		super(spriteManager, new Point(0,0));
		setAnimation("maps", mapName, 0);
		platforms = mapManager.platformsIn(mapName);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		return platforms;
	}
}
