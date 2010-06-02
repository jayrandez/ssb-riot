package riot;

import java.util.*;
import java.awt.event.*;

public class SplashScreen implements SceneProvider {
	SpriteManager spriteManager;
	FontManager fontManager;
	boolean exit = false;
	String serverString;
	
	public SplashScreen(SpriteManager spriteManager, FontManager fontManager, String serverString) {
		this.spriteManager = spriteManager;
		this.fontManager = fontManager;
		this.serverString = serverString;
	}

	public void begin() {}

	public Scene nextScene() {
		ArrayList<GameObject> splashScreen = new ArrayList<GameObject>();
		splashScreen.add(new SplashScreenImage(null, spriteManager));
		return new Scene("SplashScreen", new ArrayList<GameObject>(), splashScreen, new ArrayList<Rectangle>());
	}

	public void receivePress(int code, boolean pressed) {
		if(code == KeyEvent.VK_SPACE) {
			exit = true;
		}
	}
	
	public SceneProvider nextProvider() {
		if(exit) {
			/* Create the first provider, add it to the game window, and begin. */
			SceneProvider dummyTerminal = new DummyTerminal(spriteManager, fontManager, serverString);
			dummyTerminal.begin();
			return dummyTerminal;
		}
		return this;
	}
}
