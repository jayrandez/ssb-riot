package riot;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;

public class DummyTerminal {
	
	private ClientWindow window;
	
	private boolean exit;
	private ArrayList<GameObject> gameObjects;
	private ArrayList<GameObject> overlayGameObjects;
	private Image backgroundImage;
	private SceneProvider sceneProvider;
	
	public DummyTerminal(ClientWindow window) {
		this.window = window;
		sceneProvider = new ConnectionScreen();
		exit = false;
	}
	
	public void gameLoop() {
		BufferStrategy strategy = window.getStrategy();
		while(!exit) {
			Graphics2D g2d = (Graphics2D)strategy.getDrawGraphics();
			
			byte[] sceneData = null;/*network.getSceneData();*/
			Scene scene = new Scene(sceneData);
			renderScene(g2d, scene);
		    
		    strategy.show();
		    g2d.dispose();
		}
	}
	
	public void renderScene(Graphics2D g2d, Scene scene) {
		Dimension worldSize = scene.getWorldSize();
		BufferedImage world = new BufferedImage(worldSize.width, worldSize.height, BufferedImage.TYPE_4BYTE_ABGR);

		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		for(Sprite sprite: worldSprites) {
			sprite.drawOn(world.getGraphics());
		}
		
		Rectangle worldView = scene.getWorldView();
		int x1 = (int)worldView.getMinX();
		int y1 = (int)worldView.getMinY();
		int x2 = (int)worldView.getMaxX();
		int y2 = (int)worldView.getMaxY();
		g2d.drawImage(world, 0, 0, 1024, 769, x1, y1, x2, y2, null);
		
		ArrayList<Sprite> overlaySprites = scene.getOverlaySprites();
		for(Sprite sprite: overlaySprites) {
			sprite.drawOn(g2d);
		}
	}
}
