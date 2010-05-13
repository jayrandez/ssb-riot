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
		for(GameObject object: gameObjects) {
			Physics physics = object.getPhysics();
			worldSprites.add(physics.getSprite());
		}
	}
	
	public Scene(byte[] rawData) {
		
	}
	
	public Scene() {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		worldSize = new Dimension(1024, 768);
		worldView = new Rectangle(0, 0, 1023, 767);
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
