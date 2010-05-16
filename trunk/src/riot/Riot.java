package riot;

import riot.sceneprovider.DummyTerminal;

public class Riot {
	
	// Client > Server
	public static byte Connect;
	public static byte Disconnect;
	public static byte Establish;
	public static byte Attack;
	public static byte Special;
	public static byte Jump;
	public static byte Dodge;
	public static byte Shield;
	public static byte Direction;
	public static byte Position;
	public static byte Smash;
	
	// Server > Client
	public static byte GameSprites;
	public static byte OverlaySprites;
	public static byte ScreenLayout;
	public static byte PlayerNames;
	public static byte ServerName;
	
	// General
	public static byte MessageSeparator;
	public static byte DataSeparator;
	
	// Hardware Input
	public static byte LeftMouse;
	public static byte RightMouse;

	public static void main(String[] args) {
		SpriteManager manager = new SpriteManager("sheets");
		SceneProvider startScreen = new DummyTerminal(manager);
		SceneWindow window = new SceneWindow(startScreen);
		
		window.gameLoop();
		
		window.dispose();
		System.exit(0);
	}
}
