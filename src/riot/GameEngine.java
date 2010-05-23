package riot;

import java.io.*;
import java.net.*;
import java.util.*;

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
		
		worldObjects.add(new Map(spriteManager, mapManager, "testmap"));
	}
	
	private void handleMessage(Message message) {
		try {
			Character referral = players.get(message.sender);
			ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
			DataInputStream reader = new DataInputStream(stream);
			switch(reader.readByte()) {
				case Riot.Connect:
					Character character = new Jigglypuff(spriteManager);
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
	
	public void rectifyPlatformCollision(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBoxes().get(0);
		if(characterBounds.overlaps(platform)) {
			// If we hit the left side
			if(characterBounds.minX() < platform.minX() && characterBounds.minY() > platform.minY())
				while(characterBounds.overlaps(platform))
					characterBounds.x = characterBounds.x - 1.0;
			// If we hit the right side
			else if(characterBounds.maxX() > platform.maxX() && characterBounds.minY() > platform.minY())
				while(characterBounds.overlaps(platform))
					characterBounds.x = characterBounds.x + 1.0;
			// If we hit the top or bottom
			else
				while(characterBounds.overlaps(platform))
					characterBounds.y = characterBounds.y - 1.0;
			object.setLocation(new Point(characterBounds.x+(characterBounds.width/2), characterBounds.maxY()));
		}
	}
	
	public boolean standingOnPlatform(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBoxes().get(0);
		if(characterBounds.maxY() + 1 == platform.minY() && characterBounds.maxX() >= platform.minX() && characterBounds.minX() <= platform.maxX())
			return true;
		else
			return false;
	}
	
	public void gameLoop() {
		int frameRate = 30;
		while(true) {
			long start = System.currentTimeMillis();
			
			ArrayList<Rectangle> debugTangles = new ArrayList<Rectangle>();

			// Update Frame
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
			}
			
			// Networking Input/Output
			Message message = communicator.receiveData();
			handleMessage(message);
			Scene scene = new Scene("Test Server", worldObjects, overlayObjects, debugTangles);
			communicator.sendData(scene.serialize());
			
			// Pause Until Next Frame
			long executionTime = System.currentTimeMillis() - start;
			if(executionTime < (1000/frameRate)) {
				try {Thread.sleep((1000/frameRate) - executionTime);}
				catch(InterruptedException ex){}
			}
		}
	}
}
