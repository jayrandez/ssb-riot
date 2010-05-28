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
	SpriteManager spriteManager;
	MapManager mapManager;
	FontManager fontManager;
	
	ArrayList<Player> players;
	ArrayList<GameObject> worldObjects;
	ArrayList<GameObject> overlayObjects;
	ArrayList<Damager> damagers;
	ArrayList<Rectangle> platforms;
	
	boolean protectConcurrent;
	
	public GameEngine(SpriteManager spriteManager, MapManager mapManager, FontManager fontManager) {
		this.spriteManager = spriteManager;
		this.mapManager = mapManager;
		this.fontManager = fontManager;
		
		players = new ArrayList<Player>();
		communicator = new Communicator(true);
		communicator.acceptIncoming();
		protectConcurrent = false;
		worldObjects = new ArrayList<GameObject>();
		overlayObjects = new ArrayList<GameObject>();
		damagers = new ArrayList<Damager>();
		platforms = new ArrayList<Rectangle>();
		
		spawnWorldObject(new Map(this, spriteManager, mapManager, "summit"));
		Label label1 = new Label(this, fontManager, new Point(0, 30), "Header", "Games");
		Label label2 = new Label(this, fontManager, new Point(0, 50), "BodyText", "Local Game");
		Label label3 = new Label(this, fontManager, new Point(80, 450), "DamageMeter", "240%");
		addOverlayObject(label1);
		addOverlayObject(label2);
		addOverlayObject(label3);
	}
	
	public int numberPlayers() {
		return players.size();
	}
	
	public Map getMap() {
		return (Map)worldObjects.get(0);
	}

	/**
	 * Parses messages coming from clients and redirects them to those clients' characters
	 */
	private void handleMessage(Message message) {
		try {
			Player referral = null;
			for(Player player: players) {
				if(player.getSocket().equals(message.sender)) {
					referral = player;
					break;
				}
			}
			ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
			DataInputStream reader = new DataInputStream(stream);
			switch(reader.readByte()) {
				case Riot.Connect:
					Player player = new Player(this, 1, message.sender, spriteManager, fontManager, players.size());
					players.add(player);
					System.out.println("New player joined the game.");
					break;
				case Riot.Disconnect:
					players.remove(message.sender);
					worldObjects.remove(referral);
					System.out.println("Player left the game.");
					break;
				default:
					if(referral != null) {
						referral.handleMessage(message);
					}
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
		Rectangle characterBounds = object.getBoundingBox();
		if(characterBounds.overlaps(platform)) {
			if(characterBounds.minX() < platform.minX() && characterBounds.minY() > platform.minY())
				characterBounds.x -= platform.minX() - characterBounds.minX() + 1;
			else if(characterBounds.maxX() > platform.maxX() && characterBounds.minY() > platform.minY())
				characterBounds.x += characterBounds.maxX() - platform.maxX() + 1;
			else
				characterBounds.y -= characterBounds.maxY() - platform.minY() + 1;
			object.setLocation(new Point(characterBounds.x+(characterBounds.width/2), characterBounds.maxY()));
		}
	}
	
	/**
	 * Detects whether the character is standing on the platform by checking if its one pixel above
	 */
	public boolean standingOnPlatform(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBox();
		if(characterBounds.maxY() + 1 == platform.minY() && characterBounds.maxX() >= platform.minX() && characterBounds.minX() <= platform.maxX())
			return true;
		else
			return false;
	}
	
	public void addOverlayObject(GameObject object) {
		overlayObjects.add(object);
	}
	
	public void removeOverlayObject(GameObject object) {
		overlayObjects.remove(object);
	}
	
	/**
	 * Puts a previously created object into the world
	 */
	public void spawnWorldObject(GameObject object) {
		worldObjects.add(object);
		if(object instanceof Damager) {
			damagers.add((Damager)object);
		}
		if(object instanceof Map) {
			platforms.addAll(((Map)object).getPlatforms());
		}
	}
	
	/**
	 * Removes an object from the world
	 */
	public void removeWorldObject(GameObject object) {
		worldObjects.remove(object);
		if(object instanceof Damager) {
			damagers.remove((Damager)object);
		}
		if(object instanceof Map) {
			platforms.clear();
		}
	}
	
	/**
	 * Handles networking aspects (input / output)
	 */
	public void updateNetwork(ArrayList<Rectangle> debugTangles) {
		Message message = communicator.receiveData();
		handleMessage(message);
		Scene scene = new Scene("Test Server", worldObjects, overlayObjects, debugTangles);
		communicator.sendData(scene.serialize());
	}
	
	/**
	 * The major logic loop in the program which iterates at a frame rate of 30 fps.
	 * This steps all of the objects, checking for collisions and major events, and sending
	 * each frame to all of the clients to be drawn by their DummyTerminals.
	 */
	public void gameLoop() {
		
		// THIS FUNCTION IS REALLY BOTHERING ME. There is too much class intimacy and a
		// nasty huge instanceof conditional. SOMEONE THINK OF A STRICTLY POLYMORPHIC SOLUTION
		while(true) {
			long start = System.currentTimeMillis();
			
			/* Step all objects handling collisions and round events. */
			ArrayList<Rectangle> debugTangles = new ArrayList<Rectangle>();
			for(int i = 0; i < worldObjects.size(); i++) {
				GameObject object = worldObjects.get(i);
				if(object != null) {
					object.step();
					if(object instanceof Character) {
						boolean onPlatform = false;
						for(Rectangle platform: platforms) {
							rectifyPlatformCollision((NaturalObject)object, platform);
							if(standingOnPlatform((NaturalObject)object, platform)) {
								onPlatform = true;
								break;
							}
						}
						for(Damager damager: damagers) {
							if(object.getBoundingBox().overlaps(damager.getBoundingBox())) {
								if(object != damager.getTarget()) {
									damager.wasUsed();
									((Character)object).damage(damager);
								}
							}
						}
						((Character)object).aerial(!onPlatform);
						if(((Character)object).getBoundingBox().overlaps(getMap().getBoundingBox()) == false)
							{
								for(Player player : players)
								{
									if(player.getCharacter() == ((Character)object))
									{
										player.died();
									}
								}
							}
					}
					debugTangles.add(object.getBoundingBox());
				}
			}
			debugTangles.addAll(platforms);
			updateNetwork(debugTangles);
			
			/* Pause until the next frame. */
			try {Thread.sleep(30 - System.currentTimeMillis() + start);}
			catch(Exception ex){}
		}
	}
}
