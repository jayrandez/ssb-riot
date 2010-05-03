package riot;

import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import javax.swing.JButton;
import javax.swing.JFrame;
import jgame.JGColor;
import jgame.platform.JGEngine;

public class ClientWindow extends JFrame {
	private static final long serialVersionUID = -6417663999978098545L;
	
	private GraphicsEnvironment environment;
	private GraphicsDevice screen;
	private BufferStrategy strategy;
	
	public ClientWindow() {
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screen = environment.getDefaultScreenDevice();
		strategy = this.getBufferStrategy();
		
		setUndecorated(true);
		setResizable(false);
		
		screen.setFullScreenWindow(this);
		screen.setDisplayMode(new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN));
		
	    validate();
	}
	
	public BufferStrategy getStrategy() {
		return strategy;
	}
}
