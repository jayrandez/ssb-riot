package riot;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameEngine {
	Communicator communicator;
	ArrayList<GameObject> worldObjects;
	ArrayList<GameObject> overlayObjects;
	ArrayList<String> playerNames;
	SpriteManager manager;
	HashMap<Socket, Character> players;
	
	public GameEngine(SpriteManager manager) {
		this.manager = manager;
		
		communicator = new Communicator(true);
		communicator.acceptIncoming();
		
		worldObjects = new ArrayList<GameObject>();
		overlayObjects = new ArrayList<GameObject>();
		playerNames = new ArrayList<String>();
		players = new HashMap<Socket, Character>();
		
		worldObjects.add(new Map(manager, "firstmap"));
	}
	
	private void handleMessage(Message message) {
		try {
			Character referral = players.get(message.sender);
			ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
			DataInputStream reader = new DataInputStream(stream);
			switch(reader.readByte()) {
				case Riot.Connect:
					Character character = new Character(manager, "jigglypuff", new Size(28,32));
					players.put(message.sender, character);
					worldObjects.add(character);
					System.out.println("Created new character.");
					break;
				case Riot.Disconnect:
					players.remove(message.sender);
					worldObjects.remove(referral);
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
		// IF COLLIDING
		if(characterBounds.overlaps(platform)) {
			System.out.println("Collision!");
			// IF TOUCHING LEFT SIDE
			if(characterBounds.minX() < platform.minX() && characterBounds.minY() > platform.minY()) {
				System.out.println("Landed on Left");
				while(characterBounds.overlaps(platform)) {
					characterBounds.x = characterBounds.x - 1.0;
				}
				object.setLocation(new Point(characterBounds.x, characterBounds.y));
			}
			// IF TOUCHIG RIGHT SIDE
			else if(characterBounds.maxX() > platform.maxX() && characterBounds.minY() > platform.minY()) {
				System.out.println("Landed on Right");
				while(characterBounds.overlaps(platform)) {
					characterBounds.x = characterBounds.x + 1.0;
				}
				object.setLocation(new Point(characterBounds.x, characterBounds.y));
			}
			// IF TOUCHING TOP
			else {
				System.out.println("Landed on Top!");
				while(characterBounds.overlaps(platform)) {
					characterBounds.y = characterBounds.y - 1.0;
					
				}
				object.setLocation(new Point(characterBounds.x+(characterBounds.width/2), characterBounds.maxY()));
			}
		}
	}
	
	public void relayStandingStatus(NaturalObject object, Rectangle platform) {
		Rectangle characterBounds = object.getBoundingBoxes().get(0);
		if(characterBounds.maxY() + 1 == platform.minY() && characterBounds.maxX() >= platform.minX() && characterBounds.minX() <= platform.maxX()) {
			((Character)object).aerial(false);
		}
		else {
			((Character)object).aerial(true);
		}
	}
	
	public void gameLoop() {
		while(true) {
			long start = System.currentTimeMillis();

			// Update Frame
			Map map = (Map)worldObjects.get(0);
			for(GameObject object: worldObjects) {
				object.step();
				if(object instanceof NaturalObject) {
					for(Rectangle platform: map.getBoundingBoxes()) {
						rectifyPlatformCollision((NaturalObject)object, platform);
						relayStandingStatus((NaturalObject)object, platform);
					}
				}
			}
			
			// Networking Input/Output
			Message message = communicator.receiveData();
			handleMessage(message);
			Scene scene = new Scene("Test Server", worldObjects, overlayObjects);
			communicator.sendData(scene.serialize());
			
			// Pause Until Next Frame
			long executionTime = System.currentTimeMillis() - start;
			if(executionTime < 30) {
				try {Thread.sleep(30 - executionTime);}
				catch(InterruptedException ex){}
			}
		}
	}
}
