package riot;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The server side class that runs an actual game of SSB
 * This handles all connections, steps and manages all game objects, and takes control
 * of various other game aspects such as collisions, rounds, spawning, and deaths.
 */
public class GameEngine {
	Communicator communicator;
	ArrayList<GameObject> worldObjects;
	ArrayList<GameObject> overlayObjects;
	ArrayList<String> playerNames;
	SpriteManager spriteManager;
	MapManager mapManager;
	HashMap<Socket, Character> players;
	
	public GameEngine(SpriteManager spriteManager, MapManager mapManager) {
		this.spriteManager = spriteManager;
		this.mapManager = mapManager;
		
		communicator = new Communicator(true);
		communicator.acceptIncoming();
		
		worldObjects = new ArrayList<GameObject>();
		overlayObjects = new ArrayList<GameObject>();
		playerNames = new ArrayList<String>();
		players = new HashMap<Socket, Character>();
		
		worldObjects.add(new Map(this, spriteManager, mapManager, "testmap"));
	}
	
	/**
	 * Parses messages coming from clients and redirects them to those clients' characters
	 */
	private void handleMessage(Message message) {
		try {
			Character referral = players.get(message.sender);
			ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
			DataInputStream reader = new DataInputStream(stream);
			switch(reader.readByte()) {
				case Riot.Connect:
					Character character = new Jigglypuff(this, spriteManager);
					players.put(message.sender, character);
					worldObjects.add(character);
					System.out.println("New player joined the game.");
					break;
				case Riot.Disconnect:
					players.remove(message.sender);
					worldObjects.remove(referral);
					System.out.println("Player left the game.");
					break;
				case Riot.Direction:
					int degrees = reader.readInt();
					referral.move(degrees);
					break;
				case Riot.Attack:
					referral.attack();
					break;
				case Riot.Dodge:
					referral.dodge();
					break;
				case Riot.Jump:
					referral.jump();
					break;
				case Riot.Special:
					referral.special();
					break;
				case Riot.Shield:
					referral.shield();
					break;
			}
		}
		catch(IOException ex) {
			System.out.println("Couldn't get client's message.");
		}
	}
	
	/**
	 * Corrects a character who has collided into a map platform by moving it to the edge.
	 */
	public void rectifyPlatformCollision(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBoxes().get(0);
		if(characterBounds.overlaps(platform)) {
			/* Check if we hit the left side. */
			if(characterBounds.minX() < platform.minX() && characterBounds.minY() > platform.minY())
				while(characterBounds.overlaps(platform))
					characterBounds.x = characterBounds.x - 1.0;
			/* Check if we hit the right side. */
			else if(characterBounds.maxX() > platform.maxX() && characterBounds.minY() > platform.minY())
				while(characterBounds.overlaps(platform))
					characterBounds.x = characterBounds.x + 1.0;
			/* Check if we hit the top or bottom. */
			else
				while(characterBounds.overlaps(platform))
					characterBounds.y = characterBounds.y - 1.0;
			object.setLocation(new Point(characterBounds.x+(characterBounds.width/2), characterBounds.maxY()));
		}
	}
	
	/**
	 * Detects whether the character is standing on the platform by checking if its one pixel above
	 */
	public boolean standingOnPlatform(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBoxes().get(0);
		if(characterBounds.maxY() + 1 == platform.minY() && characterBounds.maxX() >= platform.minX() && characterBounds.minX() <= platform.maxX())
			return true;
		else
			return false;
	}
	
	/**
	 * Puts a previously created object into the world
	 */
	public void spawnWorldObject(GameObject object) {
		worldObjects.add(object);
	}
	
	/**
	 * Removes an object from the world
	 */
	public void removeWorldObject(GameObject object) {
		worldObjects.remove(object);
	}
	
	/**
	 * The major logic loop in the program which iterates at a frame rate of 30 fps.
	 * This steps all of the objects, checking for collisions and major events, and sending
	 * each frame to all of the clients to be drawn by their DummyTerminals.
	 */
	public void gameLoop() {
		int frameRate = 30;
		while(true) {
			long start = System.currentTimeMillis();
			
			ArrayList<Rectangle> debugTangles = new ArrayList<Rectangle>();

			/* Step all objects handling collisions and round events. */
			Map map = (Map)worldObjects.get(0);
			for(GameObject object: worldObjects) {
				object.step();
				if(object instanceof NaturalObject) {
					boolean continueChecking = true;
					debugTangles.add(object.getBoundingBoxes().get(0));
					for(Rectangle platform: map.getBoundingBoxes()) {
						debugTangles.add(platform);
						rectifyPlatformCollision((NaturalObject)object, platform);
						if(continueChecking) {
							boolean onPlatform = standingOnPlatform((NaturalObject)object, platform);
							((Character)object).aerial(!onPlatform);
							if(onPlatform)
								continueChecking = false;
						}
					}
				}
				if(object instanceof FollowerObject) {
					debugTangles.add(object.getBoundingBoxes().get(0));
				}
			}
			
			/* Handle networking aspects (input / output). */
			Message message = communicator.receiveData();
			handleMessage(message);
			Scene scene = new Scene("Test Server", worldObjects, overlayObjects, debugTangles);
			communicator.sendData(scene.serialize());
			
			/* Pause until the next frame. */
			long executionTime = System.currentTimeMillis() - start;
			if(executionTime < (1000/frameRate)) {
				try {Thread.sleep((1000/frameRate) - executionTime);}
				catch(InterruptedException ex){}
			}
		}
	}
}
