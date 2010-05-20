package riot;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class SceneWindow extends JFrame implements KeyListener, MouseListener, ActionListener {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private GraphicsEnvironment environment;
	private GraphicsDevice screen;
	private BufferStrategy strategy;
	
	private Timer pressTimer;
	private Timer releaseTimer;
	
	private ArrayList<Integer> pressedKeys;
	
	BufferedImage world;
	Rectangle worldView;
	
	public SceneWindow(SceneProvider provider) {
		this.provider = provider;
		
		pressedKeys = new ArrayList<Integer>();
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screen = environment.getDefaultScreenDevice();
		this.createBufferStrategy(1);
		strategy = this.getBufferStrategy();
		
		addKeyListener(this);
		addMouseListener(this);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int[] pixels = new int[16 * 16];
		Image image = tk.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		Cursor transparentCursor = tk.createCustomCursor(image, new Point(0, 0), "invisiblecursor");
		setCursor(transparentCursor);
		
		setUndecorated(true);
		setResizable(false);
		
		screen.setFullScreenWindow(this);
		screen.setDisplayMode(new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
		
	    validate();
	}
	
	public void gameLoop() {
		while(provider != null) {
			Graphics2D g2d = (Graphics2D)strategy.getDrawGraphics();
			
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			provider.receiveLocation(mouse.x, mouse.y);
			
			Scene scene = provider.nextScene();
			renderScene(g2d, scene);
		    
		    strategy.show();
		    g2d.dispose();
		    
		    if(provider.nextProvider() != provider) {
		    	provider = provider.nextProvider();
		    }
		}
	}
	
	public void renderScene(Graphics2D g2d, Scene scene) {
		if(world == null) {
			Dimension worldSize = scene.getWorldSize();
			world = new BufferedImage(worldSize.width, worldSize.height, BufferedImage.TYPE_INT_ARGB);
		}	
		if(worldView == null) {
			worldView = scene.getWorldView();
		}
		
		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		ArrayList<Sprite> overlaySprites = scene.getOverlaySprites();
		
		for(Sprite sprite: worldSprites) {
			sprite.drawOn((Graphics2D)world.getGraphics());
		}

		int x1 = (int)worldView.getMinX();
		int y1 = (int)worldView.getMinY();
		int x2 = (int)worldView.getMaxX();
		int y2 = (int)worldView.getMaxY();
		g2d.drawImage(world, 0, 0, 639, 479, x1, y1, x2, y2, null);
		
		for(Sprite sprite: overlaySprites) {
			sprite.drawOn(g2d);
		}
	}
	
	public void mousePressed(MouseEvent e) {
    	provider.receivePress(e.getButton(), true);
    }
    
    public void mouseReleased(MouseEvent e) {
    	provider.receivePress(e.getButton(), false);
    }
    
    public void keyPressed(KeyEvent e) {
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
	public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
