package riot;

import riot.sceneprovider.ConnectionScreen;

public class Riot {
	
	// Client > Server
	public static final byte Connect 		= 1;
	public static final byte Disconnect		= 2;
	public static final byte Establish		= 3;
	public static final byte Attack			= 4;
	public static final byte Special		= 5;
	public static final byte Jump			= 6;
	public static final byte Dodge			= 7;
	public static final byte Shield			= 8;
	public static final byte Direction		= 9;
	public static final byte Position		= 10;
	public static final byte Smash			= 11;
	
	// Server > Client
	public static final byte WorldSprites	= 12;
	public static final byte OverlaySprites	= 13;
	public static final byte ScreenLayout	= 14;
	public static final byte PlayerNames	= 15;
	public static final byte ServerName		= 16;
	
	// Hardware Input
	public static final byte LeftMouse		= 17;
	public static final byte RightMouse		= 18;
	
	// General
	public static final int Port 			= 48123;

	public static void main(String[] args) throws Exception {
		final SpriteManager manager = new SpriteManager("sheets");
		
		// For now we'll be testing the game using a locally run server.
		new Thread() {
			public void run() {
				new GameEngine(manager).gameLoop();
			}
		}.start();
		
		Thread.sleep(1000);
		
		SceneProvider startScreen = new ConnectionScreen(manager);
		SceneWindow window = new SceneWindow(startScreen);
		
		window.gameLoop();
		
		window.dispose();
		System.exit(0);
	}
}
