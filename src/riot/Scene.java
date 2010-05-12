package riot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Scene {

	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	ArrayList<Sprite> overlayText;
	
	Dimension worldSize;
	Rectangle worldView;
	
	public Scene(ArrayList<GameObject> gameObjects) {
		
	}
	
	public Scene(byte[] rawData) {
		
	}
	
	public byte[] serialize() {
		return null;
	}
}
