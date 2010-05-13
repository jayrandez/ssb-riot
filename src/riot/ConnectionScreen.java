package riot;

import java.awt.event.*;
import java.io.Serializable;

public class ConnectionScreen implements SceneProvider {
	SpriteManager manager;
	
	boolean exit;
	
	public ConnectionScreen(SpriteManager manager) {
		this.manager = manager;
		exit = false;
	}
	
	public SceneProvider nextProvider() {
		if(exit) {
			System.exit(0);
			return null;
		}
		return this;
	}

	public Scene nextScene() {
		return new Scene();
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
