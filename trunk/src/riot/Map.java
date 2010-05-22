package riot;

import java.util.*;

public class Map extends AnimatedObject {

	public Map(SpriteManager manager, String mapName) {
		super(manager, new Point(0,0));
		setAnimation("maps", mapName, false, 0);
	}

	public ArrayList<Rectangle> getBoundingBoxes() {
		ArrayList<Rectangle> send = new ArrayList<Rectangle>();
		send.add(new Rectangle(0,464,440,15));
		return send;
	}
}
