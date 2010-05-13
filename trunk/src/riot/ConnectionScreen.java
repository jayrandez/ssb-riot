package riot;

import java.util.ArrayList;

public class ConnectionScreen implements SceneProvider {
	SpriteManager manager;
	
	public ConnectionScreen(SpriteManager manager) {
		this.manager = manager;
	}
	
	public SceneProvider nextProvider() {
		return this;
	}

	public Scene nextScene() {
		return new Scene(new ArrayList<GameObject>());
	}
}
