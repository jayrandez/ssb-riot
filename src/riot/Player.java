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
	private SpriteManager manager;
	private GameEngine engine;
	
	public Player(GameEngine engine, int lives, Socket socket, SpriteManager manager)
	{
		this.engine = engine;
		this.manager = manager;
		this.lives = lives;
		this.socket = socket;
		SpawnPlatform platform = new SpawnPlatform(engine, manager);
		character = new Jigglypuff(engine, manager, platform);
		platform.setCharacter(character);
		engine.spawnWorldObject(platform);
		engine.spawnWorldObject(character);
	}
	
	public void died()
	{
		lives--;
		if(lives != 0)
			respawn();
	}
	public void respawn(){
		SpawnPlatform platform = new SpawnPlatform(engine, manager);
		character = new Jigglypuff(engine, manager, platform);
		platform.setCharacter(character);
		engine.spawnWorldObject(platform);
		engine.spawnWorldObject(character);
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

