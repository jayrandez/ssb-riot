package riot.sceneprovider;

import java.io.Serializable;

import riot.*;

public class ConnectionScreen implements SceneProvider {
	SpriteManager manager;
	
	public ConnectionScreen(SpriteManager manager) {
		this.manager = manager;
	}

	public void debug(Serializable message) {
		System.out.println(message);
	}

	public SceneProvider nextProvider() {
		// For now we'll be connecting to a locally hosted terminal.
		return new DummyTerminal(manager, "localhost");
	}

	public Scene nextScene() {
		return new Scene();
	}

	public void receiveLocation(int x, int y) {}
	public void receivePress(int code, boolean pressed) {}

}
