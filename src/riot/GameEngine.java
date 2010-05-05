package riot;

public abstract class GameEngine {

	abstract void addObject(GameObject object);
	abstract void removeObject(GameObject object);
	abstract void particleEffect(int index);
	abstract void gameLoop();
}
