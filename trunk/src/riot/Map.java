package riot;

import java.util.*;

public class Map extends GameObject {
	ArrayList<Rectangle> platforms;

	public Map(GameEngine engine, SpriteManager spriteManager, MapManager mapManager, String mapName) {
		super(engine, spriteManager, new Point(0,0), spriteManager.getAnimation("maps", mapName).getSize());
		setAnimation("maps", mapName);
		platforms = mapManager.platformsIn(mapName);
	}

	public ArrayList<Rectangle> getPlatforms() {
		return platforms;
	}
}
