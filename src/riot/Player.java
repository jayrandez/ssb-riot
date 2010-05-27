package riot;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.*;

public class Player {
	private int lives;
	private Character character;
	private Socket socket;
	private SpawnPlatform platform;
	
	public Player(int lives, Socket socket)
	{
		this.lives = lives;
		this.socket = socket;
	}
	
	public void died()
	{
		lives--;
	}
	public SpawnPlatform respawn(){
		if(lives != 0)
		{
			platform.setCharacter(this.getCharacter());
			return platform;
		}
		else
			return null;
	}
	
	public Character getCharacter()
	{
		return character;
	}
	public Socket getSocket()
	{
		return socket;
	}
	
	public void handleMessage(Message message)
	{
		try{
		ByteArrayInputStream stream = new ByteArrayInputStream(message.data);
		DataInputStream reader = new DataInputStream(stream);
		switch(reader.readByte()) {
			case Riot.Direction:
				int degrees = reader.readInt();
				character.move(degrees);
				break;
			case Riot.Attack:
				character.attack();
				break;
			case Riot.Dodge:
				character.dodge();
				break;
			case Riot.Jump:
				character.jump();
				break;
			case Riot.Special:
				character.special();
				break;
			case Riot.Shield:
				character.shield();
				break;
		}
		}catch(Exception ex){}
	}
}

