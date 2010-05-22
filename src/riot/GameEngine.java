package riot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import riot.gameobject.Character;
import riot.gameobject.Map;
import riot.physics.NaturalPhysics;

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
	
	private void handleMessage(DataInputStream reader, Character referral, Message message) throws IOException {
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
	
	public void gameLoop() {
		int frameNum = 0;
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			try {
				Message message = communicator.receiveData();
				Character referral = players.get(message.sender);
				ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
				DataInputStream reader = new DataInputStream(stream);
				handleMessage(reader, referral, message);
			}
			catch(IOException ex) {System.out.println("Couldn't get client's message.");}
			
			ArrayList<Character> characters = new ArrayList<Character>();
			Map map = null;
			
			for(GameObject object: worldObjects) {
				object.step();
				if(object instanceof Character)
					characters.add((Character)object);
				if(object instanceof Map)
					map = (Map)object;
			}
			
			for(Character character: characters) {
				NaturalPhysics characterPhysics = (NaturalPhysics)character.getPhysics();
				Rectangle characterBounds = characterPhysics.getBoundingBoxes().get(0);
				for(Rectangle platform: map.getPhysics().getBoundingBoxes()) {
					// IF COLLIDING
					if(characterBounds.overlaps(platform)) {
						System.out.println("Collision!");
						// IF TOUCHING LEFT SIDE
						if(characterBounds.minX() < platform.minX() && characterBounds.minY() > platform.minY()) {
							System.out.println("Landed on Left");
							while(characterBounds.overlaps(platform)) {
								characterBounds.x = characterBounds.x - 1.0;
							}
							characterPhysics.setLocation(new Location(characterBounds.x, characterBounds.y));
						}
						// IF TOUCHIG RIGHT SIDE
						else if(characterBounds.maxX() > platform.maxX() && characterBounds.minY() > platform.minY()) {
							System.out.println("Landed on Right");
							while(characterBounds.overlaps(platform)) {
								characterBounds.x = characterBounds.x + 1.0;
							}
							characterPhysics.setLocation(new Location(characterBounds.x, characterBounds.y));
						}
						// IF TOUCHING TOP
						else {
							System.out.println("Landed on Top!");
							while(characterBounds.overlaps(platform)) {
								characterBounds.y = characterBounds.y - 1.0;
								
							}
							characterPhysics.setLocation(new Location(characterBounds.x+(characterBounds.width/2), characterBounds.maxY()));
						}
						character.mapCollision();
					}
					// IF POSITIONED ABOVE
					if(characterBounds.maxY() + 1 == platform.minY() && characterBounds.maxX() >= platform.minX() && characterBounds.minX() <= platform.maxX()) {
						characterPhysics.ignoreGravity();
						character.aerial(false);
					}
					else {
						character.aerial(true);
					}
				}
			}
			
			Scene scene = new Scene("Server " + frameNum++, playerNames, worldObjects, overlayObjects);
			byte[] data = scene.serialize();
			communicator.sendData(data);
			
			long executionTime = System.currentTimeMillis() - start;
			
			if(frameNum % 60 == 0)
				System.out.println("Execution time: " + executionTime);
			
			if(executionTime < 30) {
				try {Thread.sleep(30 - executionTime);}
				catch(InterruptedException ex){}
			}
		}
	}
}
