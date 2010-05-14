package riot.sceneprovider;

import java.awt.event.*;
import java.io.Serializable;

import riot.*;
import riot.gameobject.*;
import java.util.*;
import riot.Scene;
import riot.SceneProvider;
import riot.SpriteManager;

public class CharacterSandbox implements SceneProvider {
	SpriteManager manager;
	ArrayList<GameObject> gameObjects;
	
	boolean exit;
	
	public CharacterSandbox(SpriteManager manager) {
		this.manager = manager;
		exit = false;
		gameObjects = new ArrayList<GameObject>();
		gameObjects.add(new Jigglypuff(manager));
	}
	
	public SceneProvider nextProvider() {
		if(exit) {
			System.exit(0);
			return null;
		}
		return this;
	}

	public Scene nextScene() {
		return new Scene(gameObjects);
	}
	
	public void receiveLocation(int x, int y) {
		
	}
	
	public void receivePress(int code, boolean pressed) {
		if(code == KeyEvent.VK_ESCAPE) {
			exit = true;
		}
	}

	public void debug(Serializable message) {
		System.out.println(message);
	}
}
