package riot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Scene {

	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	Dimension worldSize;
	Rectangle worldView;
	
	public Scene(ArrayList<GameObject> gameObjects) {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		
		worldSize = new Dimension(640, 480);
		worldView = new Rectangle(0, 0, 639, 479);
		
		for(GameObject object: gameObjects) {
			Physics physics = object.getPhysics();
			worldSprites.add(physics.getSprite());
			//if(object instanceof map)
			//if(object instanceof character)
		}
	}
	
	public Scene(byte[] rawData) {
		
	}
	
	public Scene() {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		worldSize = new Dimension(640, 480);
		worldView = new Rectangle(0, 0, 639, 479);
	}
	
	public byte[] serialize() {
		return null;
	}
	
	public ArrayList<Sprite> getWorldSprites() {
		return worldSprites;
	}
	
	public ArrayList<Sprite> getOverlaySprites() {
		return overlaySprites;
	}
	
	public Dimension getWorldSize() {
		return worldSize;
	}
	
	public Rectangle getWorldView() {
		return worldView;
	}
}
