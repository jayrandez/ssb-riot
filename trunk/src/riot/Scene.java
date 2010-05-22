package riot;

import java.io.*;
import java.util.*;

public class Scene {
	String serverName;
	ArrayList<String> playerNames;
	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	Size worldSize;
	Rectangle worldView;
	
	// Assembly of a Scene on the Server Side
	public Scene(String serverName, ArrayList<GameObject> worldObjects, ArrayList<GameObject> overlayObjects) {
		this.serverName = serverName;
		
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		worldSize = new Size(640, 480);
		worldView = new Rectangle(0, 0, (int)worldSize.width, (int)worldSize.height);
		
		for(GameObject object: worldObjects) {
			if(object instanceof AnimatedObject) {
				Sprite sprite = ((AnimatedObject)object).getSprite();
				worldSprites.add(sprite);
				if(object instanceof Map) {
					worldSize = new Size(sprite.width, sprite.height);
				}
			}
		}
	}

	public Scene(SpriteManager manager, byte[] rawData) {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		
		ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
		DataInputStream reader = new DataInputStream(stream);
		
		try {
			serverName = reader.readUTF();
			worldSize = new Size(reader);
			worldView = new Rectangle(reader);
			
			int count = reader.readShort();
			for(int i = 0; i < count; i++)
				worldSprites.add(new Sprite(manager, reader));

			count = reader.readShort();
			for(int i = 0; i < count; i++)
				overlaySprites.add(new Sprite(manager, reader));
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public byte[] serialize() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(stream);
		
		try {
			writer.writeUTF(serverName);
			worldSize.writeTo(writer);
			worldView.writeTo(writer);
			
			writer.writeShort(worldSprites.size());
			for(Sprite sprite: worldSprites)
				sprite.writeTo(writer);
			
			writer.writeShort(overlaySprites.size());
			for(Sprite sprite: overlaySprites)
				sprite.writeTo(writer);
			
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
	
	public Size getWorldSize() {
		return worldSize;
	}
	
	public Rectangle getWorldView() {
		return worldView;
	}
	
	public String getServerName() {
		return serverName;
	}
}
