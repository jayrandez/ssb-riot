package riot.sceneprovider;

import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import riot.*;
import java.util.*;

import riot.Riot;
import riot.Scene;
import riot.SceneProvider;
import riot.SpriteManager;

public class DummyTerminal implements SceneProvider, ActionListener {
	SpriteManager manager;
	ArrayList<GameObject> gameObjects;
	
	Communicator communicator;
	javax.swing.Timer keepAliveTimer;
	
	boolean[] directions;
	
	boolean exit;
	
	public DummyTerminal(SpriteManager manager, String hostname) {
		this.manager = manager;
		exit = false;
		directions = new boolean[4];
		for(int i = 0; i < 4; i++)
			directions[i] = false;
		
		communicator = new Communicator(false);
		if(!communicator.addOutgoing(hostname)) {
			exit = true;
		}
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
	
	public SceneProvider nextProvider() {
		if(exit) {
			System.exit(0);
			return null;
		}
		return this;
	}

	public Scene nextScene() {
		Message message = null;
		boolean send = false;
		//while(!send) {
			message = communicator.receiveData();
		//	if(message.data[0] != Riot.KeepAlive)
		//		send = true;
		//}
		return new Scene(manager, message.data);
	}
	
	public void receiveLocation(int x, int y) {
		// Send Location to Server
	}
	
	public void receivePress(int code, boolean pressed) {
		if(code == KeyEvent.VK_ESCAPE) {
			exit = true;
		}
		else {
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
				communicator.sendData(stream.toByteArray());
			}
			catch(IOException ex) {
				System.out.println("Couldn't send control data.");
			}
		}
	}
	
	private void writeDirection(DataOutputStream writer) throws IOException {
		int degrees = 0;
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
		else {
			degrees = -1;
		}
		writer.writeByte(Riot.Direction);
		writer.writeInt(degrees);
	}

	public void debug(Serializable message) {
		System.out.println(message);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}
}
