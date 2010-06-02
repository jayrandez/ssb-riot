package riot;

import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * A SceneProvider which functions by connecting to an SSB game server and directing
 * the scene for each frame to the game window. The DummyTerminal is completely passive in that
 * it does not manage or construct a scene or perform any game logic, it simply redirects what
 * the server gives it into the game window.
 */
public class DummyTerminal implements SceneProvider {
	SpriteManager spriteManager;
	FontManager fontManager;
	ArrayList<GameObject> gameObjects;
	Communicator communicator;
	boolean[] directions;
	String hostname;
	
	public DummyTerminal(SpriteManager spriteManager, FontManager fontManager, String hostname) {
		this.hostname = hostname;
		this.spriteManager = spriteManager;
		this.fontManager = fontManager;
		directions = new boolean[4];
		for(int i = 0; i < 4; i++)
			directions[i] = false;
	}

	/**
	 * Sets up the connection with the server for the first time
	 */
	public void begin() {
		communicator = new Communicator(false);
		communicator.addOutgoing(hostname);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(stream);
		try {
			writer.writeByte(Riot.Connect);
			communicator.sendData(stream.toByteArray());
		}
		catch(IOException ex) {
			System.out.println("Couldn't send control data.");
		}
	}
	
	/**
	 * Retrieves a scene from the server and relays it to the client's SceneWindow
	 */
	public Scene nextScene() {
		Message message = communicator.receiveData();
		return new Scene(spriteManager, fontManager, message.data);
	}
	
	/**
	 * Receives keyboard input and if it is the right kind, send it to the server
	 */
	public void receivePress(int code, boolean pressed) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream writer = new DataOutputStream(stream);
		try {
			switch(code) {
				case KeyEvent.VK_RIGHT:
					directions[0] = !directions[0];
					writeDirection(writer);
					break;
				case KeyEvent.VK_UP:
					directions[1] = !directions[1];
					writeDirection(writer);
					break;
				case KeyEvent.VK_LEFT:
					directions[2] = !directions[2];
					writeDirection(writer);
					break;
				case KeyEvent.VK_DOWN:
					directions[3] = !directions[3];
					writeDirection(writer);
					break;
				case KeyEvent.VK_SPACE:
					if(pressed)
						writer.writeByte(Riot.Jump);
					break;
				case KeyEvent.VK_F:
					if(pressed)
						writer.writeByte(Riot.Attack);
					break;
				case KeyEvent.VK_D:
					if(pressed)
						writer.writeByte(Riot.Special);
					break;
				case KeyEvent.VK_S:
					if(pressed)
						writer.writeByte(Riot.Dodge);
					break;
				case KeyEvent.VK_A:
					writer.writeByte(Riot.Shield);
					break;
			}
			byte[] data = stream.toByteArray();
			if(data.length > 0)
				communicator.sendData(data);
		}
		catch(IOException ex) {
			System.out.println("Couldn't send control data.");
		}
	}
	
	/**
	 * Converts combination of arrow keys to a direction in degrees
	 */
	private void writeDirection(DataOutputStream writer) throws IOException {
		/* I'm sure there's a better way to do this mathematically.
		 * Directions are numbered starting at right arrow, numbered counterclockwise.
		 */
		
		int degrees = -1;
		if(directions[0] && directions[1]) {
			degrees = 45;
		}
		else if(directions[1] && directions[2]) {
			degrees = 135;
		}
		else if(directions[2] && directions[3]) {
			degrees = 225;
		}
		else if(directions[3] && directions[0]) {
			degrees = 315;
		}
		else if(directions[0]) {
			degrees = 0;
		}
		else if(directions[1]) {
			degrees = 90;
		}
		else if(directions[2]) {
			degrees = 180;
		}
		else if(directions[3]) {
			degrees = 270;
		}
		writer.writeByte(Riot.Direction);
		writer.writeInt(degrees);
	}
	
	public SceneProvider nextProvider() {
		return this;
	}
}
