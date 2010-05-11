package riot;

public class Messages {
	public static int Port = 48123;
	
	// Client > Server
	public static byte Connect;
		// Put me on server send list.
	public static byte Disconnect;
		// Take me off server send list.
	public static byte Establish;
		// Add me as a player.
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
		// Sheet Name : Animation Name : Sprite Index : X : Y : Rotation
	public static byte OverlaySprites;
		// Sheet Name : Animation Name : Sprite Index : X : Y : Rotation
	public static byte ScreenLayout;
		// X : Y : Width : Height
	public static byte PlayerNames;
		// Name : Name : Name ...
	public static byte ServerName;
		// Name
}
