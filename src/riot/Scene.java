package riot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import riot.gameobject.Character;
import riot.gameobject.Map;


public class Scene {

	String serverName;
	ArrayList<String> playerNames;
	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	Dimension worldSize;
	Rectangle worldView;
	
	// Creation of a blank scene for testing
	public Scene() {
		serverName = "Local Test Server";
		playerNames = new ArrayList<String>();
		playerNames.add("Zapata");
		playerNames.add("Solidpenguin");
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		worldSize = new Dimension(640, 480);
		worldView = new Rectangle(0, 0, 640, 480);
	}
	
	// Assembly of a Scene on the Server Side
	public Scene(String serverName, ArrayList<String> playerNames, ArrayList<GameObject> worldObjects, ArrayList<GameObject> overlayObjects) {
		this.serverName = serverName;
		this.playerNames = playerNames;
		
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		
		worldSize = new Dimension(640, 480);
		
		ArrayList<Character> characters = new ArrayList<Character>();
		
		for(GameObject object: worldObjects) {
			Physics physics = object.getPhysics();
			Sprite sprite = physics.getSprite();
			worldSprites.add(sprite);
			
			if(object instanceof Map) {
				worldSize = new Dimension(sprite.image.getWidth(), sprite.image.getHeight());
			}
			
			if(object instanceof Character) {
				characters.add((Character)object);
			}
		}
		
		for(GameObject object: overlayObjects) {
			Physics physics = object.getPhysics();
			overlaySprites.add(physics.getSprite());
		}
		
		worldView = getBestFitView(characters, worldSize);
	}
	
	private Rectangle getBestFitView(ArrayList<Character> characters, Dimension worldSize) {
		int worldWidth = (int)worldSize.getWidth();
		int worldHeight = (int)worldSize.getHeight();
		if(characters.size() > 0) {
			int farLeft = characters.get(0).getPhysics().getX();
			int farRight = characters.get(0).getPhysics().getX();
			int farTop = characters.get(0).getPhysics().getY();
			int farBottom = characters.get(0).getPhysics().getY();
			for(int i = 1; i < characters.size(); i++) {
				int x = characters.get(i).getPhysics().getX();
				int y = characters.get(i).getPhysics().getY();
				if(x < farLeft)
					farLeft = x;
				if(x > farRight)
					farRight = x;
				if(y > farBottom)
					farBottom = y;
				if(y < farTop)
					farTop = y;
			}
			farLeft -= 100;
			farRight += 100;
			farTop -= 100;
			farBottom += 100;
			
			if(farRight > worldWidth)
				farRight = worldWidth;
			if(farLeft < 0)
				farLeft = 0;
			if(farBottom > worldHeight)
				farBottom = worldHeight;
			if(farTop < 0)
				farTop = 0;
			
			int width = farRight - farLeft;
			int height = farBottom - farTop;
			int centerX = farLeft + (width / 2);
			int centerY = farTop + (height / 2);
			
			int resultingWidth = height * 4 / 3;
			if(resultingWidth >= width) {
				farLeft = centerX - (resultingWidth / 2);
				farRight = centerX + (resultingWidth / 2);
				width = resultingWidth;
			}
			else {
				int resultingHeight = width * 3 / 4;
				farTop = centerY - (resultingHeight / 2);
				farBottom = centerY + (resultingHeight / 2);
				height = resultingHeight;
			}
			
			return new Rectangle(farLeft, farTop, width, height);
		}
		else {
			return new Rectangle(0, 0, 640, 480);
		}
	}
	
	// Assembly of the Scene on the Client Side
	public Scene(SpriteManager manager, byte[] rawData) {
		ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
		DataInputStream reader = new DataInputStream(stream);
		
		playerNames = new ArrayList<String>();
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		
		try {
			while(reader.available() > 0) {
				switch(reader.readByte()) {
					case Riot.ServerName: {
						serverName = reader.readUTF();
						break;
					}
					case Riot.PlayerNames: {
						int count = reader.readInt();
						for(int i = 0; i < count; i++)
							playerNames.add(reader.readUTF());
						break;
					}
					case Riot.ScreenLayout:
						worldSize = new Dimension(reader.readInt(), reader.readInt());
						worldView = new Rectangle(reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt());
						break;
					case Riot.WorldSprites: {
						int count = reader.readInt();
						for(int i = 0; i < count; i++) {
							Sprite sprite = new Sprite(manager, reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt());
							worldSprites.add(sprite);
						}
						break;
					}
					case Riot.OverlaySprites: {
						int count = reader.readInt();
						for(int i = 0; i < count; i++) {
							Sprite sprite = new Sprite(manager, reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt(), reader.readInt());
							overlaySprites.add(sprite);
						}
						break;
					}
				}
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	// Serialization of the Scene for Transmittance
	public byte[] serialize() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(stream);
		
		try {
			// Write Server Name Information
			writer.writeByte(Riot.ServerName);
			writer.writeUTF(serverName);
			
			// Write Player Name Information
			writer.writeByte(Riot.PlayerNames);
			writer.writeInt(playerNames.size());
			for(String playerName: playerNames) {
				writer.writeUTF(playerName);
			}
			
			// Write Screen Layout Information
			writer.writeByte(Riot.ScreenLayout);
			writer.writeInt(worldSize.height);
			writer.writeInt(worldSize.width);
			writer.writeInt((int)worldView.getMinX());
			writer.writeInt((int)worldView.getMinY());
			writer.writeInt((int)worldView.getWidth());
			writer.writeInt((int)worldView.getHeight());
			
			// Write World Sprite Information
			writer.writeByte(Riot.WorldSprites);
			writer.writeInt(worldSprites.size());
			for(Sprite sprite: worldSprites) {
				writer.writeInt(sprite.index);
				writer.writeInt(sprite.frame);
				writer.writeInt(sprite.x);
				writer.writeInt(sprite.y);
				writer.writeInt(sprite.rotation);
			}
			
			// Write Overlay Sprite Information
			writer.writeByte(Riot.OverlaySprites);
			writer.writeInt(overlaySprites.size());
			for(Sprite sprite: overlaySprites) {
				writer.writeInt(sprite.index);
				writer.writeInt(sprite.frame);
				writer.writeInt(sprite.x);
				writer.writeInt(sprite.y);
				writer.writeInt(sprite.rotation);
			}
			
			return stream.toByteArray();
		}
		catch(IOException ex) {
			return new byte[0];
		}
	}
	
	public ArrayList<Sprite> getWorldSprites() {
		return worldSprites;
	}
	
	public ArrayList<Sprite> getOverlaySprites() {
		return overlaySprites;
	}
	
	public Dimension getWorldSize() {
		return worldSize;
	}
	
	public Rectangle getWorldView() {
		return worldView;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public ArrayList<String> getPlayerNames() {
		return playerNames;
	}
	
	public static void main(String[] arguments) throws Exception {
		
		// Timed Trials Seem to Be Good After Analysis
		// We should be able to max 30 FPS to 5 Players
		// Simultaneously over TCP with Detail as High
		// As 75 Sprites per Scene With =0%= Data Loss
		
		SpriteManager manager = new SpriteManager("sheets");
		
		Thread.sleep(2000);
		
		
		System.out.println("\n\nTime 10 Seconds... Go!\n\n");
		for(int x = 0; x < 10; x++) {
		
		System.out.println("150 High-Detail Trials (5P * 30FPS) & 75 Sprites.");
		System.out.println("5ms fake network latency per scene");
		for(int j = 0; j < 150; j++) {
			ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
			for(int i = 0; i < 75; i++) {
				gameObjects.add(new Character(manager));
			}
			ArrayList<String> players = new ArrayList<String>();
			players.add("Zapata");
			players.add("Solidpenguin");
			Scene scene = new Scene("Local Test Server", players, gameObjects, new ArrayList<GameObject>());
			byte[] data = scene.serialize();
			//System.out.println("Length: " + data.length);
			for(int i = 0; i < data.length; i++) {
				//System.out.print("" + (int)data[i] + ", ");
			}
			
			// Fake latency 5 ms
			Thread.sleep(5);
			
			//System.out.println();
			Scene recon = new Scene(manager, data);
			//System.out.println("Server Name: " + recon.getServerName());
			ArrayList<String> players2 = recon.getPlayerNames();
			for(String name: players2) {
				//System.out.print(name + ", ");
			}
			//System.out.println();
			//System.out.println("World Sprites: " + recon.getWorldSprites().size());
			//System.out.println("Overlay Sprites: " + recon.getOverlaySprites().size());
			//System.out.println("World Size: " + recon.getWorldSize());
			//System.out.println("World View: " + recon.getWorldView());
		}
		System.out.println("Done with the 150. Was it less than a second?");
		
		}
		System.out.println("\n\n\n\nSTOP!");
	}
}
