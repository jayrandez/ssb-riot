package riot;

import java.util.ArrayList;

import riot.gameobject.Character;
import riot.gameobject.Map;

public class GameEngine {
	Communicator communicator;
	ArrayList<GameObject> worldObjects;
	ArrayList<GameObject> overlayObjects;
	ArrayList<String> players;
	SpriteManager manager;
	
	public GameEngine(SpriteManager manager) {
		this.manager = manager;
		
		communicator = new Communicator();
		communicator.acceptIncoming();
		
		worldObjects = new ArrayList<GameObject>();
		overlayObjects = new ArrayList<GameObject>();
		players = new ArrayList<String>();
		
		players.add("Zapata");
		players.add("Solidpenguin");
		
		worldObjects.add(new Map(manager, "firstmap"));
		worldObjects.add(new Character(manager));
	}
	
	public void gameLoop() {
		new Thread() {
			public void run() {
				while(true) {
					Scene scene = new Scene("Local Test Server", players, worldObjects, overlayObjects);
					byte[] data = scene.serialize();
					communicator.sendData(data);
					try {Thread.sleep(100);}
					catch(InterruptedException ex){}
				}
			}
		}.start();
		
		while(true) {
			for(GameObject object: worldObjects) {
				object.step();
			}
			for(GameObject object: overlayObjects) {
				object.step();
			}
			try {Thread.sleep(10);}
			catch(InterruptedException ex){}
		}
	}
}
