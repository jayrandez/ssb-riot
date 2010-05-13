package riot;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SceneWindow extends JFrame {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private SceneProvider provider;
	private GraphicsEnvironment environment;
	private GraphicsDevice screen;
	private BufferStrategy strategy;
	
	public SceneWindow(SceneProvider provider) {
		this.provider = provider;
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screen = environment.getDefaultScreenDevice();
		strategy = this.getBufferStrategy();
		
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
}
