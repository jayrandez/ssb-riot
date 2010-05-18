package riot.sceneprovider;

import java.awt.event.*;
import java.io.Serializable;

import riot.*;
import java.util.*;
import riot.Scene;
import riot.SceneProvider;
import riot.SpriteManager;

public class DummyTerminal implements SceneProvider {
	SpriteManager manager;
	ArrayList<GameObject> gameObjects;
	
	Communicator communicator;
	
	boolean exit;
	
	public DummyTerminal(SpriteManager manager, String hostname) {
		this.manager = manager;
		exit = false;
		
		communicator = new Communicator();
		if(!communicator.addOutgoing(hostname)) {
			exit = true;
		}
	}
	
	public SceneProvider nextProvider() {
		if(exit) {
			System.exit(0);
			return null;
		}
		return this;
	}

	public Scene nextScene() {
		Message message = communicator.receiveData();
		return new Scene(manager, message.data);
	}
	
	public void receiveLocation(int x, int y) {
		// Send Location to Server
	}
	
	public void receivePress(int code, boolean pressed) {
		if(code == KeyEvent.VK_ESCAPE) {
			exit = true;
		}
		else {
			// Send Press to Server
		}
	}

	public void debug(Serializable message) {
		System.out.println(message);
	}
}
