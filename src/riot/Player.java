package riot;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.*;

import javax.swing.Timer;

public class Player {
	private int lives;
	private Character character;
	private Socket socket;
	private SpawnPlatform platform;
	private SpriteManager spriteManager;
	private FontManager fontManager;
	private GameEngine engine;
	private Timer timer;
	private int playerNumber;
	private boolean dead = false;
	
	public Player(GameEngine engine, int lives, Socket socket, SpriteManager spriteManager, FontManager fontManager, int playerNumber)
	{
		System.out.println("Player Number: " + playerNumber);
		this.engine = engine;
		this.spriteManager = spriteManager;
		this.fontManager = fontManager;
		this.lives = lives;
		this.socket = socket;
		this.playerNumber = playerNumber;
		SpawnPlatform platform = new SpawnPlatform(engine, spriteManager);
		character = new Jigglypuff(engine, spriteManager, platform);
		platform.setCharacter(character);
		engine.spawnWorldObject(platform);
		engine.spawnWorldObject(character);
		timer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
				dead = false;
			}
		});
	}
	
	public void died()
	{
		dead = true;
		lives--;
		if(lives != 0) {
			engine.removeWorldObject(character);
			respawn();
			timer.start();
		}
	}
	public void respawn(){
		SpawnPlatform platform = new SpawnPlatform(engine, spriteManager);
		character = new Jigglypuff(engine, spriteManager, platform);
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
	
	public int getPlayerNumber() {
		return playerNumber;
	}
	
	public void handleMessage(Message message)
	{	
		if(dead == false)
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
}

