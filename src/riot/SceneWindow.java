package riot;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class SceneWindow extends JFrame implements KeyListener, MouseListener {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private GraphicsEnvironment environment;
	private GraphicsDevice screen;
	private BufferStrategy strategy;
	
	public SceneWindow(SceneProvider provider) {
		this.provider = provider;
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
		Dimension worldSize = scene.getWorldSize();
		BufferedImage world = new BufferedImage(worldSize.width, worldSize.height, BufferedImage.TYPE_4BYTE_ABGR);

		ArrayList<Sprite> worldSprites = scene.getWorldSprites();
		for(Sprite sprite: worldSprites) {
			sprite.drawOn((Graphics2D)world.getGraphics());
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
	
	public void mousePressed(MouseEvent e) {
    	provider.receivePress(e.getButton(), true);
    }
    
    public void mouseReleased(MouseEvent e) {
    	provider.receivePress(e.getButton(), false);
    }
    
    public void keyPressed(KeyEvent e) {
    	provider.receivePress(e.getKeyCode(), true);
    }
    
    public void keyReleased(KeyEvent e) {
    	provider.receivePress(e.getKeyCode(), false);
    }
    
    public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
