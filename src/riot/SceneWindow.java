package riot;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class SceneWindow extends JFrame implements KeyListener {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private ArrayList<Integer> pressedKeys;
	private boolean fullscreen;
	private BufferStrategy strategy;
	private Canvas windowCanvas;
	
	GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice screen = environment.getDefaultScreenDevice();
	
	Image world;
	Rectangle worldView;
	Graphics2D worldGraphics;
	
	public SceneWindow(SceneProvider provider, boolean fullscreen) {
		this.provider = provider;
		this.fullscreen = fullscreen;
		this.pressedKeys = new ArrayList<Integer>();
		this.windowCanvas = new Canvas();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Toolkit tk = Toolkit.getDefaultToolkit();
		int[] pixels = new int[16 * 16];
		Image image = tk.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = tk.createCustomCursor(image, new java.awt.Point(0, 0), "invisiblecursor");
		setCursor(transparentCursor);
		addKeyListener(this);

		setUndecorated(false);
		setResizable(false);
		setSize(new Dimension(645, 507));
		setIgnoreRepaint(true);
		screenSetup();
		
		provider.begin();
	}
	
	void screenSetup() {
		if(fullscreen) {
			remove(windowCanvas);
			createBufferStrategy(1);
			strategy = getBufferStrategy();
			screen.setFullScreenWindow(this);
    		screen.setDisplayMode(new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
    		setVisible(true);
		}
		else {
			add(windowCanvas);
			windowCanvas.createBufferStrategy(1);
			strategy = windowCanvas.getBufferStrategy();
			screen.setFullScreenWindow(null);
			setVisible(true);
		}
	}
	
	public void drawingLoop() {
		while(provider != null) {
			Graphics g2d = strategy.getDrawGraphics();
			if(g2d != null) {
				Scene scene = provider.nextScene();
				renderScene(g2d, scene);
				g2d.dispose();
			    strategy.show();
			}
		}
	}
	
	public void renderScene(Graphics g2d, Scene scene) {
		if(world == null || worldGraphics == null) {
			Size worldSize = scene.getWorldSize();
			GraphicsConfiguration gc = screen.getDefaultConfiguration();
			world = gc.createCompatibleImage((int)worldSize.width, (int)worldSize.height, Transparency.BITMASK);
			worldGraphics = (Graphics2D)world.getGraphics();
		}
		
		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		ArrayList<Rectangle> debugTangles = scene.getDebugTangles();
		worldView = scene.getWorldView();
		
		for(Sprite sprite: worldSprites) {
			sprite.drawTo(worldGraphics);
		}
		
		worldGraphics.setColor(Color.white);
		for(Rectangle rectangle: debugTangles) {
			worldGraphics.drawRect((int)rectangle.x, (int)rectangle.y, (int)rectangle.width-1, (int)rectangle.height-1);
		}

		int x1 = (int)worldView.minX();
		int y1 = (int)worldView.minY();
		int x2 = (int)worldView.maxX();
		int y2 = (int)worldView.maxY();

		g2d.drawImage(world, 0, 0, 639, 479, x1, y1, x2, y2, null);
		g2d.drawString("" + scene.getServerName(), 10, 10);
	}
    
    public void keyPressed(KeyEvent e) {
    	if(e.getKeyCode() == KeyEvent.VK_F1) {
    		fullscreen = !fullscreen;
    		screenSetup();
    	}
    	if(!pressedKeys.contains(e.getKeyCode())) {
    		pressedKeys.add(e.getKeyCode());
    		provider.receivePress(e.getKeyCode(), true);
    	}
    }
    
    public void keyReleased(KeyEvent e) {
    	provider.receivePress(e.getKeyCode(), false);
    	for(int i = 0; i < pressedKeys.size(); i++) {
    		if(pressedKeys.get(i) == e.getKeyCode()) {
    			pressedKeys.remove(i);
    			break;
    		}
    	}
    }
    
    public void keyTyped(KeyEvent e) {}
}
