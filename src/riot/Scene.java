package riot;

import java.io.*;
import java.util.*;

public class Scene {
	String serverName;
	ArrayList<String> playerNames;
	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	ArrayList<Rectangle> debugTangles;
	Size worldSize;
	Rectangle worldView;
	
	// Assembly of a Scene on the Server Side
	public Scene(String serverName, ArrayList<GameObject> worldObjects, ArrayList<GameObject> overlayObjects, ArrayList<Rectangle> debugTangles) {
		this.serverName = serverName;
		this.debugTangles = debugTangles;
		
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();

		ArrayList<Character> characters = new ArrayList<Character>();
		for(GameObject object: worldObjects) {
			if(object instanceof AnimatedObject) {
				Sprite sprite = ((AnimatedObject)object).getSprite();
				worldSprites.add(sprite);
				if(object instanceof Map) {
					worldSize = new Size(sprite.width, sprite.height);
				}
			}
			if(object instanceof Character) {
				characters.add((Character)object);
			}
		}
		
		Rectangle boundingBox = bestFit(characters, worldSize, 175);
		worldView = bestView(boundingBox, worldSize);
	}
	
	private Rectangle bestView(Rectangle boundingBox, Size worldSize) {
		boundingBox.forceRatioKeepCentered(4.0/3.0);
		Rectangle worldRectangle = worldSize.toRectangle();
		if(boundingBox.area() >= worldRectangle.area()) {
			return worldRectangle;
		}
		double leftHang = worldRectangle.minX() - boundingBox.minX();
		double rightHang = boundingBox.maxX() - worldRectangle.maxX();
		double topHang = worldRectangle.minY() - boundingBox.minY();
		double bottomHang = boundingBox.maxY() - worldRectangle.maxY();
		if(leftHang > 0) {
			boundingBox.x += leftHang;
		}
		if(rightHang > 0) {
			boundingBox.x -= rightHang;
		}
		if(topHang > 0) {
			boundingBox.y += topHang;
		}
		if(bottomHang > 0) {
			boundingBox.y -= bottomHang;
		}
		return boundingBox;
	}
	
	private Rectangle bestFit(ArrayList<Character> characters, Size worldSize, int padding) {
		if(characters.size() > 0) {
			double leftBound = characters.get(0).getLocation().x;
			double topBound = characters.get(0).getLocation().y;
			double rightBound = characters.get(0).getLocation().x;
			double bottomBound = characters.get(0).getLocation().y;
			for(Character character: characters) {
				Point location = character.getLocation();
				if(location.x < leftBound)
					leftBound = location.x;
				if(location.x > rightBound)
					rightBound = location.x;
				if(location.y < topBound)
					topBound = location.y;
				if(location.y > bottomBound)
					bottomBound = location.y;
			}
			leftBound -= padding;
			topBound -= padding;
			rightBound += padding;
			bottomBound += padding;
			return new Rectangle(leftBound, topBound, rightBound-leftBound, bottomBound-topBound);
		}
		return worldSize.toRectangle();
	}

	public Scene(SpriteManager manager, byte[] rawData) {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		debugTangles = new ArrayList<Rectangle>();
		
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
			
			count = reader.readShort();
			for(int i = 0; i < count; i++)
				debugTangles.add(new Rectangle(reader));
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
			
			writer.writeShort(debugTangles.size());
			for(Rectangle rectangle: debugTangles)
				rectangle.writeTo(writer);
			
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
	
	public ArrayList<Rectangle> getDebugTangles() {
		return debugTangles;
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
