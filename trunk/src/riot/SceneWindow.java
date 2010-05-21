package riot;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class SceneWindow extends JFrame implements KeyListener {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private GraphicsEnvironment environment;
	private GraphicsDevice screen;
	private BufferStrategy strategy;
	
	private ArrayList<Integer> pressedKeys;
	
	BufferedImage world;
	Rectangle worldView;
	
	public SceneWindow(SceneProvider provider, boolean defaultFullScreen) {
		this.provider = provider;
		pressedKeys = new ArrayList<Integer>();
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screen = environment.getDefaultScreenDevice();
		this.createBufferStrategy(1);
		strategy = this.getBufferStrategy();
		
		addKeyListener(this);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int[] pixels = new int[16 * 16];
		Image image = tk.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = tk.createCustomCursor(image, new Point(0, 0), "invisiblecursor");
		setCursor(transparentCursor);
		
		setUndecorated(true);
		setResizable(false);
		setSize(new Dimension(640, 480));
		setIgnoreRepaint(true);
		setVisible(true);
		
		if(defaultFullScreen) {
			screen.setFullScreenWindow(this);
    		screen.setDisplayMode(new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		provider.begin();
	}
	
	public void gameLoop() {
		while(provider != null) {
			Graphics2D g2d = (Graphics2D)strategy.getDrawGraphics();
			
			Scene scene = provider.nextScene();
			renderScene(g2d, scene);
		    
		    strategy.show();
		    g2d.dispose();
		}
	}
	
	public void renderScene(Graphics2D g2d, Scene scene) {
		if(world == null) {
			Dimension worldSize = scene.getWorldSize();
			world = new BufferedImage(worldSize.width, worldSize.height, BufferedImage.TYPE_INT_ARGB);
		}	
		
		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		
		for(Sprite sprite: worldSprites) {
			sprite.drawOn((Graphics2D)world.getGraphics());
		}

		worldView = scene.getWorldView();
		int x1 = (int)worldView.getMinX();
		int y1 = (int)worldView.getMinY();
		int x2 = (int)worldView.getMaxX();
		int y2 = (int)worldView.getMaxY();
		g2d.drawImage(world, 0, 0, 639, 479, x1, y1, x2, y2, null);
		
		
		/*g2d.setColor(Color.white);
		g2d.fillRect(0,0,200,50);
		g2d.setColor(Color.black);
		g2d.drawString("" + scene.getServerName(), 10, 10);*/
	}
	
	public void mousePressed(MouseEvent e) {
    	provider.receivePress(e.getButton(), true);
    }
    
    public void mouseReleased(MouseEvent e) {
    	provider.receivePress(e.getButton(), false);
    }
    
    public void keyPressed(KeyEvent e) {
    	if(e.getKeyCode() == KeyEvent.VK_F1) {
    		screen.setFullScreenWindow(this);
    		screen.setDisplayMode(new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
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
