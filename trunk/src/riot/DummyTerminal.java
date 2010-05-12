package riot;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;

public class DummyTerminal {
	
	private ClientWindow window;
	private ClientNetwork network;
	
	private boolean exit;
	private ArrayList<GameObject> gameObjects;
	private ArrayList<GameObject> overlayGameObjects;
	private Image backgroundImage;
	
	public DummyTerminal(ClientWindow window) {
		this.window = window;
		exit = false;
	}
	
	public void gameLoop() {
		BufferStrategy strategy = window.getStrategy();
		while(!exit) {
			Graphics2D g2d = (Graphics2D)strategy.getDrawGraphics();
			
			byte[] sceneData = network.getSceneData();
			Scene scene = new Scene(sceneData);
			renderScene(g2d, scene);
		    
		    strategy.show();
		    g2d.dispose();
		}
	}
	
	public void renderScene(Graphics2D g2d, Scene scene) {
		BufferedImage buffer = new BufferedImage(1024, 768, BufferedImage.TYPE_4BYTE_ABGR);
		// get size of map from scene
		// BufferedImage buffer = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		// use scene to draw world elements
		// get clipping rectangle from scene
		// drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) 
		// use scene to draw overlay elements
	}
}
