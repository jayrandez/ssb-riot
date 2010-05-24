package riot;

/**
 * An interface which can be used to put content into the game window when implemented
 */
public interface SceneProvider {
	public Scene nextScene();
	public void begin();
	public void receivePress(int code, boolean pressed);
}
