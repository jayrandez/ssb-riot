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

public class ClientEngine extends GameEngine {
	
	private ClientNetwork network;
	private ClientWindow window;
	private boolean gameFinished;
	private ArrayList<GameObject> gameObjects;
	private ArrayList<GameObject> overlayGameObjects;
	private Image backgroundImage;
	
	public ClientEngine(ClientWindow window) {
		this.window = window;
		this.network = network;
		
		
		
		gameObjects = new ArrayList<GameObject>();
		gameFinished = false;
	}
	
	public void gameLoop() {
		BufferStrategy strategy = window.getStrategy();
		while(!gameFinished) {
			Graphics2D g2d = (Graphics2D)strategy.getDrawGraphics();
			
			stepAll();
		    renderAll(g2d);
		    
		    strategy.show();
		    g2d.dispose();
		    
		    try {Thread.sleep(4);}
		    catch(InterruptedException e) {}
		}
	}
	
	public void stepAll() {
		for(GameObject object: gameObjects) {
			object.step();
		}
	}
	
	public void renderAll(Graphics2D g2d) {
		BufferedImage buffer = new BufferedImage(1024, 768, BufferedImage.TYPE_4BYTE_ABGR);
		
		//ArrayList<Avatar> avatars = new ArrayList<Avatar>();
		for(GameObject object: gameObjects) {
			/*if(object instanceof Avatar) {
				avatars.add((Avatar)object);
			}*/
		}
		for(GameObject object: gameObjects) {
			object.draw(g2d);
		}
		//else 
		//drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) 
		
	}

	@Override
	void addObject(GameObject object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void particleEffect(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void removeObject(GameObject object) {
		// TODO Auto-generated method stub
		
	}
}
