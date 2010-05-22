package riot;

import java.util.*;

public class Riot {
	
	// Client > Server
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
	
	// Server > Client
	public static final byte WorldSprites	= 12;
	public static final byte OverlaySprites	= 13;
	public static final byte ScreenLayout	= 14;
	public static final byte PlayerNames	= 15;
	public static final byte ServerName		= 16;
	
	// General
	public static final int Port 			= 48123;
	public static final boolean Left			= true;
	public static final boolean Right			= false;

	public static void main(String[] args) throws Exception {
		final SpriteManager manager = new SpriteManager("sheets");
		Scanner scanner = new Scanner(System.in);
		String line;
		
		System.out.print("\nStart a server at localhost? [Y/n] ");
		line = scanner.nextLine();
		if(line.equals("") || line.equals("Y") || line.equals("y")) {
			new Thread() {
				public void run() {
					new GameEngine(manager).gameLoop();
				}
			}.start();
		}
		
		System.out.print("Connect to server [localhost]: ");
		line = scanner.nextLine();
		if(line.equals(""))
			line = "localhost";
		
		boolean fullscreen = false;
		if(args.length > 0 && args[0].equals("-fullscreen"))
			fullscreen = true;
		
		SceneProvider startScreen = new DummyTerminal(manager, line);
		SceneWindow window = new SceneWindow(startScreen, fullscreen);
		window.gameLoop();
		window.dispose();
		
		System.exit(0);
	}
}
