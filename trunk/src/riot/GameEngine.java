package riot;

import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import riot.gameobject.Character;
import riot.gameobject.Map;

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
				Character character = new Character(manager, "jigglypuff");
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
			
			try {
				Message message = communicator.receiveData();
				Character referral = players.get(message.sender);
				ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
				DataInputStream reader = new DataInputStream(stream);
				handleMessage(reader, referral, message);
			}
			catch(IOException ex) {System.out.println("Couldn't get client's message.");}
			
			for(GameObject object: worldObjects) {
				object.step();
			}
			for(GameObject object: overlayObjects) {
				object.step();
			}
			
			Scene scene = new Scene("Test Server " + frameNum, playerNames, worldObjects, overlayObjects);
			byte[] data = scene.serialize();
			communicator.sendData(data);
			
			try {Thread.sleep(40);}
			catch(InterruptedException ex){}
			
			frameNum++;
		}
	}
}
