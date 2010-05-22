package riot.physics;

import riot.Location;
import riot.Physics;
import riot.Rectangle;
import riot.Sprite;
import riot.SpriteManager;
import java.util.*;
import java.awt.*;

public class MapPhysics extends AnimationPhysics {

	public MapPhysics(SpriteManager manager, String mapName) {
		super(manager, new Location(0,0));
		super.setAnimation("maps", mapName, false, 0);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		ArrayList<Rectangle> boundingBoxes = new ArrayList<Rectangle>();
		boundingBoxes.add(new Rectangle(0,464,450,15));
		return boundingBoxes;
	}
}
