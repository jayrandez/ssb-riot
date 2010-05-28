package riot;

public class Rhinovirus extends Character {

	public Rhinovirus(GameEngine engine, SpriteManager manager, SpawnPlatform platform, Player player) {
		super(engine, manager, "rhinovirus", new Size(200,200), 20, platform, player);
	}
}
