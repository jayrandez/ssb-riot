package riot;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class SceneWindow extends JFrame implements KeyListener {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private ArrayList<Integer> pressedKeys;
	private boolean fullscreen;
	private boolean debugtangles;
	private BufferStrategy strategy;
	private Canvas windowCanvas;
	
	GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice screen = environment.getDefaultScreenDevice();
	
	Image world;
	Image worldSwap;
	Rectangle worldView;
	Graphics2D worldGraphics;
	Graphics2D worldSwapGraphics;
	
	public SceneWindow(SceneProvider provider) {
		this.provider = provider;
		this.fullscreen = false;
		this.pressedKeys = new ArrayList<Integer>();
		this.windowCanvas = new Canvas();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Toolkit tk = Toolkit.getDefaultToolkit();
		int[] pixels = new int[16 * 16];
		Image image = tk.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = tk.createCustomCursor(image, new java.awt.Point(0, 0), "invisiblecursor");
		setCursor(transparentCursor);
		addKeyListener(this);
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
		}
		else {
			add(windowCanvas);
			windowCanvas.createBufferStrategy(1);
			strategy = windowCanvas.getBufferStrategy();
			screen.setFullScreenWindow(null);
			int windowWidth = 645;
			int windowHeight = 507;
			int displayWidth = screen.getDisplayMode().getWidth();
			int displayHeight = screen.getDisplayMode().getHeight();
			setBounds((displayWidth/2)-(windowWidth/2), (displayHeight/2)-(windowHeight/2), windowWidth, windowHeight);
			setResizable(false);
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
			this.requestFocus();
		}
	}
	
	public void renderScene(Graphics g2d, Scene scene) {
		if(world == null || worldGraphics == null) {
			Size worldSize = scene.getWorldSize();
			GraphicsConfiguration gc = screen.getDefaultConfiguration();
			world = gc.createCompatibleImage((int)worldSize.width, (int)worldSize.height, Transparency.BITMASK);
			worldSwap = gc.createCompatibleImage((int)worldSize.width, (int)worldSize.height, Transparency.BITMASK);
			worldGraphics = (Graphics2D)world.getGraphics();
			worldSwapGraphics = (Graphics2D)worldSwap.getGraphics();
		}
		
		this.setTitle("SSB: Riot!  --  " + scene.getServerName());
		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		ArrayList<Rectangle> debugTangles = scene.getDebugTangles();
		worldView = scene.getWorldView();
		
		for(Sprite sprite: worldSprites) {
			sprite.drawTo(worldGraphics);
		}
		
		if(debugtangles) {
			BufferedImage temp = new BufferedImage(world.getWidth(null), world.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
			temp.getGraphics().drawImage(world, 0, 0, null);
			worldGraphics.drawImage(temp, 0, 0, null);
			worldGraphics.setStroke(new BasicStroke(2));
			worldGraphics.setColor(Color.red);
			for(Rectangle rectangle: debugTangles) {
				worldGraphics.drawRect((int)rectangle.x, (int)rectangle.y, (int)rectangle.width-1, (int)rectangle.height-1);
			}
		}

		int x1 = (int)worldView.minX();
		int y1 = (int)worldView.minY();
		int x2 = (int)worldView.maxX();
		int y2 = (int)worldView.maxY();

		g2d.drawImage(world, 0, 0, 639, 479, x1, y1, x2, y2, null);
	}
    
    public void keyPressed(KeyEvent e) {
    	switch(e.getKeyCode()) {
    	case KeyEvent.VK_F1:
    		fullscreen = !fullscreen;
    		screenSetup();
    		break;
    	case KeyEvent.VK_F2:
    		debugtangles = !debugtangles;
    		break;
    	default:
    		if(!pressedKeys.contains(e.getKeyCode())) {
        		pressedKeys.add(e.getKeyCode());
        		provider.receivePress(e.getKeyCode(), true);
        	}
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
