package riot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Riot {

	public static void main(String[] args) {
		SpriteManager manager = new SpriteManager("sheets");
		SceneProvider startScreen = new ConnectionScreen(manager);
		SceneWindow window = new SceneWindow(startScreen);
		
		window.gameLoop();
		
		window.dispose();
		System.exit(0);
	}
}
