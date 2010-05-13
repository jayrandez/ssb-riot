package riot;

import java.util.ArrayList;
import java.awt.event.*;

public class ConnectionScreen implements SceneProvider {
	SpriteManager manager;
	
	boolean exit;
	
	public ConnectionScreen(SpriteManager manager) {
		this.manager = manager;
		exit = false;
	}
	
	public SceneProvider nextProvider() {
		if(exit == true) {
			return null;
		}
		return this;
	}

	public Scene nextScene() {
		return new Scene(new ArrayList<GameObject>());
	}
	
	public void receiveLocation(int x, int y) {
		
	}
	
	public void receivePress(int code, boolean pressed) {
		if(code == KeyEvent.VK_ESCAPE) {
			exit = true;
		}
	}
}
