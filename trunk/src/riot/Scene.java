package riot;

import java.io.*;
import java.util.*;

/**
 * The overall representation of a single visual frame which can be drawn by the SceneWindow
 * It contains mostly a list of sprites as well as view/zoom information.
 */
public class Scene {
	String serverName;
	ArrayList<String> playerNames;
	ArrayList<Sprite> worldSprites;
	ArrayList<Sprite> overlaySprites;
	ArrayList<Rectangle> debugTangles;
	Size worldSize;
	Rectangle worldView;
	
	public Scene(String serverName, ArrayList<GameObject> worldObjects, ArrayList<GameObject> overlayObjects, ArrayList<Rectangle> debugTangles) {
		this.serverName = serverName;
		this.debugTangles = debugTangles;
		
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();

		ArrayList<Character> characters = new ArrayList<Character>();
		for(GameObject object: worldObjects) {
			Sprite sprite = object.getSprite();
			if(sprite != null) {
				worldSprites.add(sprite);
				if(object instanceof Map) {
					worldSize = new Size(sprite.width, sprite.height);
				}
			}
			if(object instanceof Character) {
				characters.add((Character)object);
			}
		}
		
		for(GameObject object: overlayObjects) {
			Sprite sprite = object.getSprite();
			if(sprite != null) {
				overlaySprites.add(sprite);
			}
		}

		Rectangle boundingBox = bestFit(characters, worldSize, 175);
		worldView = bestView(boundingBox, worldSize);
	}
	
	/**
	 * Determines how to zoom the view given the bounding box around all need-to-see objects
	 */
	private Rectangle bestView(Rectangle boundingBox, Size worldSize) {
		/* Force the view to the correct aspect ratio. */
		boundingBox.forceRatioKeepCentered(4.0/3.0);
		
		/* If the view is too big, just show the whole screen. */
		Rectangle worldRectangle = worldSize.toRectangle();
		if(boundingBox.area() >= worldRectangle.area()) {
			return worldRectangle;
		}
		
		/* If aspect ratio corrected rectangle is hanging off the screen, bring it inwards. */
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
	
	/**
	 * Determines the bounding box around all objects necessary to be on screen
	 */
	private Rectangle bestFit(ArrayList<Character> characters, Size worldSize, int padding) {
		if(characters.size() > 0) {
			/* Get the extreme left, right, top and bottom of a rectangle containing all sprites. */
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
			
			/* Add requested padding to this rectangle. */
			leftBound -= padding;
			topBound -= padding;
			rightBound += padding;
			bottomBound += padding;
			return new Rectangle(leftBound, topBound, rightBound-leftBound, bottomBound-topBound);
		}
		return worldSize.toRectangle();
	}

	/**
	 * Constructs a scene from data that has been transmitted over the network
	 */
	public Scene(SpriteManager spriteManager, FontManager fontManager, byte[] rawData) {
		worldSprites = new ArrayList<Sprite>();
		overlaySprites = new ArrayList<Sprite>();
		debugTangles = new ArrayList<Rectangle>();
		
		ByteArrayInputStream stream = new ByteArrayInputStream(rawData);
		DataInputStream reader = new DataInputStream(stream);
		
		/* Reassemble data from the stream */
		try {
			serverName = reader.readUTF();
			worldSize = new Size(reader);
			worldView = new Rectangle(reader);
			
			int count = reader.readShort();
			for(int i = 0; i < count; i++)
				if(reader.readByte() == Riot.StandardSprite)
					worldSprites.add(new Sprite(spriteManager, reader));
				else
					worldSprites.add(new TextSprite(fontManager, reader));

			count = reader.readShort();
			for(int i = 0; i < count; i++)
				if(reader.readByte() == Riot.StandardSprite)
					overlaySprites.add(new Sprite(spriteManager, reader));
				else
					overlaySprites.add(new TextSprite(fontManager, reader));
			
			count = reader.readShort();
			for(int i = 0; i < count; i++)
				debugTangles.add(new Rectangle(reader));
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Writes all of this scene's data to a byte buffer to be sent over the network
	 */
	public byte[] serialize() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(stream);
		
		/* Write data to the stream */
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
	
	/**
	 * Returns a list of all the sprites in the world
	 */
	public ArrayList<Sprite> getWorldSprites() {
		return worldSprites;
	}
	
	/**
	 * Returns a list of all the sprites on the HUD
	 */
	public ArrayList<Sprite> getOverlaySprites() {
		return overlaySprites;
	}
	
	/**
	 * Returns a list of all debugging rectangles
	 */
	public ArrayList<Rectangle> getDebugTangles() {
		return debugTangles;
	}
	
	/**
	 * Returns the size of the world / map
	 */
	public Size getWorldSize() {
		return worldSize;
	}
	
	/**
	 * Returns a rectangle representing the visible area of the world / map
	 */
	public Rectangle getWorldView() {
		return worldView;
	}
	
	/**
	 * Returns the name of the server
	 */
	public String getServerName() {
		return serverName;
	}
}
