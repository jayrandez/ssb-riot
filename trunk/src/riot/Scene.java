package riot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import riot.gameobject.Map;
import riot.physics.*;


public class Scene {
	String serverName;
	ArrayList<String> playerNames;
	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	Size worldSize;
	Rectangle worldView;
	
	// Assembly of a Scene on the Server Side
	public Scene(String serverName, ArrayList<String> playerNames, ArrayList<GameObject> worldObjects, ArrayList<GameObject> overlayObjects) {
		this.serverName = serverName;
		this.playerNames = playerNames;
		
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		worldSize = new Size(640, 480);
		worldView = new Rectangle(0, 0, (int)worldSize.width, (int)worldSize.height);
		
		for(GameObject object: worldObjects) {
			Physics physics = object.getPhysics();
			if(physics instanceof AnimationPhysics) {
				Sprite sprite = ((AnimationPhysics)physics).getSprite();
				worldSprites.add(sprite);
				if(object instanceof Map) {
					worldSize = new Size(sprite.width, sprite.height);
				}
			}
		}
	}

	// Assembly of the Scene on the Client Side
	public Scene(SpriteManager manager, byte[] rawData) {
		serverName = "";
		playerNames = new ArrayList<String>();
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		
		ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
		DataInputStream reader = new DataInputStream(stream);
		
		try {
			// Read Server Name
			int count = reader.readShort();
			for(int i = 0; i < count; i++)
				serverName += reader.readChar();
			// Read World Information
			worldSize = new Size(reader.readShort(), reader.readShort());
			worldView = new Rectangle(reader.readShort(), reader.readShort(), reader.readShort(), reader.readShort());
			// Read World Sprites
			count = reader.readShort();
			for(int i = 0; i < count; i++) {
				Sprite sprite = new Sprite(manager, reader.readShort(), reader.readShort(), reader.readShort(), reader.readShort(), reader.readShort(), reader.readBoolean());
				worldSprites.add(sprite);
			}
			// Read Overlay Sprites
			count = reader.readShort();
			for(int i = 0; i < count; i++) {
				Sprite sprite = new Sprite(manager, reader.readShort(), reader.readShort(), reader.readShort(), reader.readShort(), reader.readShort(), reader.readBoolean());
				overlaySprites.add(sprite);
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
			writer.writeShort(serverName.length());
			for(int i = 0; i < serverName.length(); i++)
				writer.writeChar(serverName.charAt(i));
			// Write Screen Layout Information
			writer.writeShort((int)worldSize.width);
			writer.writeShort((int)worldSize.height);
			writer.writeShort((int)worldView.x);
			writer.writeShort((int)worldView.y);
			writer.writeShort((int)worldView.width);
			writer.writeShort((int)worldView.height);
			// Write World Sprite Information
			writer.writeShort(worldSprites.size());
			for(Sprite sprite: worldSprites) {
				writer.writeShort(sprite.index);
				writer.writeShort(sprite.frame);
				writer.writeShort(sprite.x);
				writer.writeShort(sprite.y);
				writer.writeShort(sprite.rotation);
				writer.writeBoolean(sprite.flipped);
			}
			// Write Overlay Sprite Information
			writer.writeShort(overlaySprites.size());
			for(Sprite sprite: overlaySprites) {
				writer.writeShort(sprite.index);
				writer.writeShort(sprite.frame);
				writer.writeShort(sprite.x);
				writer.writeShort(sprite.y);
				writer.writeShort(sprite.rotation);
				writer.writeBoolean(sprite.flipped);
			}
			return stream.toByteArray();
		}
		catch(IOException ex) {
			System.out.println("BAD WRITE");
			return new byte[0];
		}
	}
	
	public ArrayList<Sprite> getWorldSprites() {
		return worldSprites;
	}
	
	public ArrayList<Sprite> getOverlaySprites() {
		return overlaySprites;
	}
	
	public Size getWorldSize() {
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
}
