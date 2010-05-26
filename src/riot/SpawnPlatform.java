package riot;

public class SpawnPlatform extends NaturalObject {
	Character character;

	public SpawnPlatform(GameEngine engine, SpriteManager manager) {
		super(engine, manager, new Point(40, 70), new Size(0,0), 0);
		setAnimation("miscsprites", "spawnplatform");
		setMovement(10, 0);
	}

	public void step() {
		super.step();
		if(getLocation().x < 20) {
			setMovement(10, 0);
		}
		if(getLocation().x > getEngine().getMap().getSize().width) {
			setMovement(10, 180);
		}
		if(character != null) {
			character.setLocation(getLocation());
			character.setMovement(0,0);
		}
	}
	
	public void setCharacter(Character character) {
		this.character = character;
	}
	
	public void dropCharacter() {
		getEngine().removeWorldObject(this);
	}
}
