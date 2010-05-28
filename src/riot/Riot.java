package riot;

import java.util.*;

/**
 * The Riot class is the entry class to the game.
 */
public class Riot {
	
	/* Various constants that are used throughout the game */
	public static final byte Connect		= 1;
	public static final byte Disconnect		= 2;
	public static final byte Establish		= 3;
	public static final byte Control		= 4;
	public static final byte KeepAlive		= 5;
	public static final byte Attack			= 6;
	public static final byte Special		= 7;
	public static final byte Jump			= 8;
	public static final byte Dodge			= 9;
	public static final byte Shield			= 10;
	public static final byte Direction		= 11;
	public static final byte WorldSprites	= 12;
	public static final byte OverlaySprites	= 13;
	public static final byte ScreenLayout	= 14;
	public static final byte PlayerNames	= 15;
	public static final byte ServerName		= 16;
	public static final byte Pause			= 17;
	public static final byte Resume			= 18;
	public static final byte StandardSprite = 19;
	public static final byte TextSprite		= 20;
	public static final boolean Left		= true;
	public static final boolean Right		= false;
	public static final boolean LeftAlign 	= false;
	public static final boolean CenterAlign = true;
	public static final int Port 			= 48123;

	/**
	 * The main function creates a SceneWindow (the game's window) and creates the starting provider
	 * to go with it as well as loading up spritesheets and maps. For now, we're just starting and
	 * connecting to local servers.
	 */
	public static void main(String[] args) throws Exception {
		final SpriteManager spriteManager = new SpriteManager("sheets");
		final MapManager mapManager = new MapManager("maps");
		final FontManager fontManager = new FontManager("fonts");
		
		Scanner scanner = new Scanner(System.in);
		String line = "";
		
		System.out.print("\nStart a server at localhost? [Y/n] ");
		line = scanner.nextLine();
		if(line.equals("") || line.equals("Y") || line.equals("y")) {
			new Thread() {
				public void run() {
					/* Create a game server in a new thread. */
					new GameEngine(spriteManager, mapManager, fontManager).gameLoop();
				}
			}.start();
		}
		
		System.out.print("Connect to server [localhost]: ");
		line = scanner.nextLine();
		System.out.println();
		if(line.equals(""))
			line = "localhost";
		
		/* Create the first provider, add it to the game window, and begin. */
		SceneProvider startScreen = new DummyTerminal(spriteManager, fontManager, line);
		SceneWindow window = new SceneWindow(startScreen);
		window.drawingLoop();
		window.dispose();
		
		System.exit(0);
	}
}
